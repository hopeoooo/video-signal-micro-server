package com.central.user.controller;

import cn.hutool.core.date.DateUtil;
import com.central.common.annotation.LoginUser;
import com.central.common.constant.UserConstant;
import com.central.common.model.*;
import com.central.common.vo.SysMoneyVO;
import com.central.user.service.ISysTansterMoneyLogService;
import com.central.user.service.ISysUserMoneyService;
import com.central.user.service.ISysUserService;
import com.central.user.util.RedissLockUtil;
import com.central.user.vo.SysUserMoneyVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

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
public class SysUserMoneyController {
    @Autowired
    private ISysUserMoneyService userMoneyService;

    @Autowired
    private ISysUserService iSysUserService;
    @Autowired
    private ISysTansterMoneyLogService iSysTansterMoneyLogService;

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

    @ApiOperation(value = "设置玩家金额")
    @PostMapping("/playerMoney")
    public Result<Boolean> updateMoney(@RequestBody SysMoneyVO sysMoneyVO){
        log.info("udpate money for player {}",sysMoneyVO.getUid());
        SysUserMoney sysUserMoney = userMoneyService.lambdaQuery().eq(SysUserMoney::getUserId,sysMoneyVO.getUid()).one();
        Boolean result = Boolean.FALSE;
        if(sysUserMoney != null){
            log.info("+++++++++  udpate user money  {}",sysMoneyVO.getUid());
            result = userMoneyService.lambdaUpdate().eq(SysUserMoney::getUserId,sysMoneyVO.getUid()).set(SysUserMoney::getMoney,sysMoneyVO.getUserMoney()).update();
        } else{
            log.info("+++++++++  udpate user money  {}",sysMoneyVO.getUid());
            SysUserMoney userMoney = new SysUserMoney();
            userMoney.setMoney(sysMoneyVO.getUserMoney());
            userMoney.setUserId(sysMoneyVO.getUid());
            userMoney.setUnfinishedCode(BigDecimal.ZERO);
            result = userMoneyService.save(userMoney);
        }
        log.info("+++++++++  udpate user money  {}",result);
        return Result.succeed(result);
    }

    @ApiOperation(value = "保存")
    @PostMapping("/save")
    public Result<SysUserMoney> save(@RequestBody SysUserMoney sysUserMoney) {
        SysUserMoney saveSysUserMoney = userMoneyService.saveCache(sysUserMoney);
        return Result.succeed(saveSysUserMoney);
    }

    @ApiOperation(value = "上下分")
    @PostMapping("/transterMoney")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "money", value = "金额", required = true, dataType = "BigDecimal"),
            @ApiImplicitParam(name = "remark", value = "备注", dataType = "String"),
            @ApiImplicitParam(name = "transterType", value = "1：人工上分，0：人工下分", required = true, dataType = "Boolean")
    })
    public Result<SysUserMoney> transterMoney(Long userId, BigDecimal money, String remark, Boolean transterType) throws Exception {
        if(money.compareTo(BigDecimal.ZERO) <= 0){
            return Result.failed("参数错误");
        }
        String redisKey = UserConstant.redisKey.SYS_USER_MONEY_MONEY_LOCK  + userId;
        boolean moneyLock = RedissLockUtil.tryLock(redisKey, UserConstant.redisKey.WAIT_TIME, UserConstant.redisKey.LEASE_TIME);
        try {
            if(moneyLock){
                SysUserMoney sysUserMoney = userMoneyService.findByUserId(userId);
                SysUser sysUser = iSysUserService.selectById(userId);
                if (sysUserMoney == null || sysUser == null) {
                    return Result.failed("用户不存在或钱包错误");
                }
                SysUserMoney saveSysUserMoney = userMoneyService.transterMoney(sysUserMoney, money, transterType, remark, sysUser);
                userMoneyService.syncPushMoneyToWebApp(userId);
                return Result.succeed(saveSysUserMoney);
            }else{
                return Result.failed("上下分请求太过频繁");
            }
        }catch (Exception e){
            throw new Exception("用户上下分异常，param = {" + userId + "}, error = {" + e.getMessage() + "}");
        }finally {
            RedissLockUtil.unlock(redisKey);
        }
    }


    @ApiOperation("登录用户领取洗码")
    @GetMapping("/receiveWashCode")
    public Result<String> receiveWashCode(@LoginUser SysUser user) {
        //获取登陆用户
        Long userId = user.getId();
        String moneyKey = UserConstant.redisKey.SYS_USER_MONEY_MONEY_LOCK + userId;
        boolean moneyLock = RedissLockUtil.tryLock(moneyKey, UserConstant.redisKey.WAIT_TIME, UserConstant.redisKey.LEASE_TIME);
        String washCodeKey = UserConstant.redisKey.SYS_USER_MONEY_WASH_CODE_LOCK + userId;
        boolean washCodeLock = RedissLockUtil.tryLock(washCodeKey, UserConstant.redisKey.WAIT_TIME, UserConstant.redisKey.LEASE_TIME);
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
            userMoneyService.syncPushMoneyToWebApp(userId);
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
            return Result.succeed("成功领取金额" + washCode.stripTrailingZeros().toPlainString());
        } finally {
            RedissLockUtil.unlock(moneyKey);
            RedissLockUtil.unlock(washCodeKey);
        }
    }
}
