package com.central.user.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.model.SysUserMoney;
import com.central.user.service.ISysUserMoneyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
