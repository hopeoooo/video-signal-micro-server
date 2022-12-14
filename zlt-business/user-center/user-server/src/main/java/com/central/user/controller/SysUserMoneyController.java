package com.central.user.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.central.common.annotation.LoginUser;
import com.central.common.model.*;
import com.central.common.redis.constant.RedisKeyConstant;
import com.central.common.redis.lock.RedissLockUtil;
import com.central.common.vo.SysMoneyVO;
import com.central.config.dto.BetMultipleDto;
import com.central.config.feign.ConfigService;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import com.central.user.model.vo.RankingListVo;
import com.central.user.service.ISysTansterMoneyLogService;
import com.central.user.service.ISysUserAuditService;
import com.central.user.service.ISysUserMoneyService;
import com.central.user.service.ISysUserService;
import com.central.user.model.vo.SysUserMoneyVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户钱包表
 *
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
@Slf4j
@RestController
@RequestMapping("/userMoney")
@Api(tags = "用户钱包")
@Validated
public class SysUserMoneyController {
    @Autowired
    private ISysUserMoneyService userMoneyService;

    @Autowired
    private ISysUserService iSysUserService;
    @Autowired
    private ISysTansterMoneyLogService iSysTansterMoneyLogService;
    @Autowired
    private PushService pushService;



    @ApiOperation(value = "查询当前登录用户的钱包")
    @GetMapping("/getMoney")
    public Result<SysUserMoneyVo> getMoney(@LoginUser SysUser user) {
        SysUserMoney sysUserMoney = userMoneyService.findByUserId(user.getId());
        if (sysUserMoney == null) {
            sysUserMoney = new SysUserMoney();
        }
        SysUserMoneyVo vo = new SysUserMoneyVo();
        BeanUtils.copyProperties(sysUserMoney, vo);
        return Result.succeed(vo);
    }

    @ApiOperation(value = "查询当前登录用户的钱包")
    @GetMapping("/getMoneyByUserName")
    public Result<SysUserMoneyVo> getMoneyByUserName(@RequestParam("userName") String userName) {
        SysUser user = iSysUserService.selectByUsername(userName);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        SysUserMoney sysUserMoney = userMoneyService.findByUserId(user.getId());
        if (sysUserMoney == null) {
            sysUserMoney = new SysUserMoney();
        }
        SysUserMoneyVo vo = new SysUserMoneyVo();
        BeanUtils.copyProperties(sysUserMoney, vo);
        return Result.succeed(vo);
    }

    @ApiOperation(value = "上下分")
    @PostMapping("/transterMoney")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "money", value = "金额", required = true, dataType = "BigDecimal"),
            @ApiImplicitParam(name = "transterType", value = "6：人工下分,5：人工上分，3:派彩，4:下注，8:商户API加点，9:商户API扣点", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "remark", value = "备注", dataType = "String"),
            @ApiImplicitParam(name = "traceId", value = "第三方交易编号", dataType = "String"),
            @ApiImplicitParam(name = "betId", value = "注单号", dataType = "String"),
            @ApiImplicitParam(name = "auditMultiple", value = "打码倍数", dataType = "BigDecimal"),
    })
    public Result<SysUserMoney> transterMoney(Long userId, BigDecimal money, String remark, Integer transterType,
                                              String traceId, String betId,BigDecimal auditMultiple) {
        if (money.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.failed("参数错误");
        }
        CapitalEnum capitalEnum = CapitalEnum.fingCapitalEnumType(transterType);
        if (capitalEnum == CapitalEnum.DEFAULT) {
            return Result.failed("操作类型错误");
        }
        SysUser sysUser = iSysUserService.selectById(userId);
        if (sysUser == null) {
            return Result.failed("用户不存在");
        }
        String redisKey = RedisKeyConstant.SYS_USER_MONEY_MONEY_LOCK + userId;
        boolean moneyLock = RedissLockUtil.tryLock(redisKey, RedisKeyConstant.WAIT_TIME, RedisKeyConstant.LEASE_TIME);
        SysUserMoney saveSysUserMoney = null;
        try {
            if (!moneyLock) {
                return Result.failed("上下分请求太过频繁");
            }
            SysUserMoney sysUserMoney = userMoneyService.findByUserId(userId);
            if (sysUserMoney == null) {
                return Result.failed("用户钱包不存在");
            }
            if (transterType == CapitalEnum.BUSINESS_SUB.getType() && money.compareTo(sysUserMoney.getMoney()) == 1) {
                return Result.failed("扣点金额不能大于剩余金额");
            } else if (transterType == CapitalEnum.BET.getType() && money.compareTo(sysUserMoney.getMoney()) == 1) {
                return Result.failed("下注金额不能大于剩余金额");
            }


            saveSysUserMoney = userMoneyService.transterMoney(sysUserMoney, money, transterType, remark, traceId, sysUser, betId, auditMultiple);


        } catch (Exception e) {
            log.error("用户上下分异常,userId:{},money:{},remark:{},transterType{},traceId{},betId:{}", userId, money, remark, transterType, traceId, betId);
            return Result.failed("操作失败");
        } finally {
            RedissLockUtil.unlock(redisKey);
        }
        //推送到首页余额变化
        userMoneyService.syncPushMoneyToWebApp(userId, sysUser.getUsername());
        //推送余额变化到桌台
        userMoneyService.syncPushMoneyToTableNum(userId, sysUser.getUsername());
        return Result.succeed(saveSysUserMoney);
    }



    @ApiOperation(value = "设置玩家金额")
    @PostMapping("/playerMoney")
    public Result<Boolean> updateMoney(@RequestBody SysMoneyVO sysMoneyVO){
        log.info("udpate money for player {}",sysMoneyVO.getUid());
        SysUserMoney sysUserMoney = userMoneyService.lambdaQuery().eq(SysUserMoney::getUserId,sysMoneyVO.getUid()).one();
        if(sysUserMoney != null){
            log.info("+++++++++  udpate user money  {}",sysMoneyVO.getUid());
            sysUserMoney.setMoney(sysMoneyVO.getUserMoney());
            userMoneyService.updateCache(sysUserMoney);
        } else{
            log.info("+++++++++  udpate user money  {}",sysMoneyVO.getUid());
            SysUserMoney userMoney = new SysUserMoney();
            userMoney.setMoney(sysMoneyVO.getUserMoney());
            userMoney.setUserId(sysMoneyVO.getUid());
            userMoney.setUnfinishedCode(BigDecimal.ZERO);
            userMoneyService.saveCache(userMoney);
        }
        return Result.succeed();
    }

    @ApiOperation(value = "保存")
    @PostMapping("/save")
    public Result<SysUserMoney> save(@RequestBody SysUserMoney sysUserMoney) {
        SysUserMoney saveSysUserMoney = userMoneyService.saveCache(sysUserMoney);
        return Result.succeed(saveSysUserMoney);
    }

    @ApiOperation(value = "更新洗码金额")
    @PostMapping("/updateWashCode")
    public Result updateWashCode(@NotNull(message = "userId不允许为空") @RequestParam("userId") Long userId, @NotNull(message = "washCode不允许为空") @RequestParam("washCode") BigDecimal washCode) {
        if (washCode.compareTo(BigDecimal.ZERO) < 1) {
            return Result.failed("洗码金额必须大于0");
        }
        String washCodeKey = RedisKeyConstant.SYS_USER_MONEY_WASH_CODE_LOCK + userId;
        boolean washCodeLock = RedissLockUtil.tryLock(washCodeKey, RedisKeyConstant.WAIT_TIME, RedisKeyConstant.LEASE_TIME);
        if (!washCodeLock) {
            log.error("洗码锁获取失败,userId={},washCode={}", userId, washCode);
            return Result.failed("更新洗码金额失败");
        }
        try {
            SysUserMoney userMoney = userMoneyService.findByUserId(userId);
            BigDecimal oldWashCode = userMoney.getWashCode() == null ? BigDecimal.ZERO : userMoney.getWashCode();
            userMoney.setWashCode(oldWashCode.add(washCode));
            SysUserMoney sysUserMoney = userMoneyService.updateCache(userMoney);
        } finally {
            RedissLockUtil.unlock(washCodeKey);
        }
        return Result.succeed();
    }

    @ApiOperation(value = "更新未完成打码量")
    @PostMapping("/updateUnfinishedCode")
    public Result updateUnfinishedCode(@NotNull(message = "用户ID不允许为空") @RequestParam("userId") Long userId, @NotNull(message = "打码量不允许为空") @RequestParam("unfinishedCode") BigDecimal unfinishedCode) {
        if (unfinishedCode.compareTo(BigDecimal.ZERO) < 1) {
            return Result.failed("打码金额必须大于0");
        }
        SysUser sysUser = iSysUserService.selectById(userId);
        String unfinishedCodeKey = RedisKeyConstant.SYS_USER_MONEY_FLOW_CODE_LOCK + userId;
        boolean unfinishedCodeLock = RedissLockUtil.tryLock(unfinishedCodeKey, RedisKeyConstant.WAIT_TIME, RedisKeyConstant.LEASE_TIME);
        if (!unfinishedCodeLock) {
            log.error("打码锁获取失败,userId={},unfinishedCode={}", userId, unfinishedCode);
            return Result.failed("更新打码金额失败");
        }
        try {
            SysUserMoney userMoney = userMoneyService.findByUserId(userId);
            BigDecimal flowCode = userMoney.getUnfinishedCode().subtract(unfinishedCode);
            BigDecimal unfinishedCodeAfter = flowCode.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : flowCode;
            userMoney.setUnfinishedCode(unfinishedCodeAfter);
            SysUserMoney sysUserMoney = userMoneyService.updateCache(userMoney);
            //推送到首页余额变化
            userMoneyService.syncPushMoneyToWebApp(userId, sysUser.getUsername());
        } finally {
            RedissLockUtil.unlock(unfinishedCodeKey);
        }
        return Result.succeed();
    }

    @ApiOperation("登录用户领取洗码")
    @GetMapping("/receiveWashCode")
    public Result<String> receiveWashCode(@LoginUser SysUser user) {
        //获取登陆用户
        Long userId = user.getId();
        String moneyKey = RedisKeyConstant.SYS_USER_MONEY_MONEY_LOCK + userId;
        boolean moneyLock = RedissLockUtil.tryLock(moneyKey, RedisKeyConstant.WAIT_TIME, RedisKeyConstant.LEASE_TIME);
        String washCodeKey = RedisKeyConstant.SYS_USER_MONEY_WASH_CODE_LOCK + userId;
        boolean washCodeLock = RedissLockUtil.tryLock(washCodeKey, RedisKeyConstant.WAIT_TIME, RedisKeyConstant.LEASE_TIME);
        if (!moneyLock || !washCodeLock) {
            return Result.failed("领取失败");
        }
        try {
            SysUserMoney userMoney = userMoneyService.findByUserId(userId);
            BigDecimal washCode = BigDecimal.ZERO;
            if (userMoney != null && userMoney.getWashCode() != null) {
                washCode = userMoney.getWashCode();
            }
            if (washCode.compareTo(BigDecimal.ONE) == -1) {
                return Result.failed("金额小于1,不能领取");
            }
            BigDecimal money = userMoney.getMoney();
            userMoneyService.receiveWashCode(userMoney);
            //推送到首页余额变化
            userMoneyService.syncPushMoneyToWebApp(userId, user.getUsername());
            //推送余额变化到桌台
            userMoneyService.syncPushMoneyToTableNum(userId, user.getUsername());
            //记录账变
            SysTansterMoneyLog sysTansterMoneyLog = new SysTansterMoneyLog();
            sysTansterMoneyLog.setUserId(userId);
            sysTansterMoneyLog.setUserName(user.getUsername());
            sysTansterMoneyLog.setMoney(washCode);
            sysTansterMoneyLog.setBeforeMoney(money);
            sysTansterMoneyLog.setAfterMoney(money.add(washCode));
            sysTansterMoneyLog.setOrderType(CapitalEnum.WASH_CODE.getType());
            sysTansterMoneyLog.setOrderNo("XM" + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS"));
            iSysTansterMoneyLogService.syncSave(sysTansterMoneyLog);
            return Result.succeedDynamic("成功领取洗码金额" , washCode.stripTrailingZeros().toPlainString());
        } finally {
            RedissLockUtil.unlock(moneyKey);
            RedissLockUtil.unlock(washCodeKey);
        }
    }

    /**
     * 私发用户钱包消息
     *
     * @return
     */
    @ApiOperation(value = "webSocket私发登录用户钱包消息")
    @GetMapping("/pushMoney")
    public PushResult<SysUserMoneyVo> pushMoney(@LoginUser SysUser user) {
        SysUserMoney sysUserMoney = userMoneyService.findByUserId(user.getId());
        if (sysUserMoney == null) {
            sysUserMoney = new SysUserMoney();
        }
        SysUserMoneyVo vo = new SysUserMoneyVo();
        BeanUtils.copyProperties(sysUserMoney, vo);
        PushResult<SysUserMoneyVo> pushResult = PushResult.succeed(vo, SocketTypeConstant.MONEY,"用户钱包推送成功");
        Result<String> push = pushService.sendOneMessage(user.getUsername(), JSONObject.toJSONString(pushResult));
        log.info("用户钱包userName:{},推送结果:{}", user.getUsername(), push);
        return pushResult;
    }

    @ApiOperation(value = "查询商户下所有用户的余额")
    @GetMapping("/getSumMoneyByParent")
    @ApiImplicitParam(name = "parent", value = "父级", required = true, dataType = "String")
    public Result<BigDecimal> getSumMoneyByParent(@NotBlank(message = "parent不允许为空") @RequestParam(value = "parent") String parent) {
        BigDecimal sumMoney = userMoneyService.getSumMoneyByParent(parent);
        return Result.succeed(sumMoney);
    }

    @ApiOperation(value = "今日排行-富豪榜")
    @GetMapping("/getRichList")
    public Result<List<RankingListVo>> getRichList() {
        List<RankingListVo> list = userMoneyService.getRichList();
        return Result.succeed(list);
    }
}
