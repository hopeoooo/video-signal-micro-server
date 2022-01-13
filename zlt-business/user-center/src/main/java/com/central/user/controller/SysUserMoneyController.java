package com.central.user.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.constant.CommonConstant;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.model.SysUserMoney;
import com.central.user.service.ISysUserMoneyService;
import com.central.user.util.RedissLockUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

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

    @ApiOperation(value = "查询当前登录用户的钱包")
    @GetMapping("/getMoney")
    public Result<SysUserMoney> getMoney(@LoginUser SysUser user) {
        SysUserMoney sysUserMoney = userMoneyService.findByUserId(user.getId());
        if (sysUserMoney == null) {
            sysUserMoney = new SysUserMoney();
        }
        return Result.succeed(sysUserMoney);
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
        String redisKey = CommonConstant.redisKet.REDIS_TRANSTER_USER_KEY + "_" + userId;
        boolean moneyLock = RedissLockUtil.tryLock(redisKey, CommonConstant.redisKet.WAIT_TIME, -1);
        try {
            if(moneyLock){
                SysUserMoney sysUserMoney = userMoneyService.findByUserId(userId);
                if (sysUserMoney == null) {
                    return Result.failed("用户不存在或钱包错误");
                }
                SysUserMoney saveSysUserMoney = userMoneyService.transterMoney(sysUserMoney, money, transterType, remark);
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


}
