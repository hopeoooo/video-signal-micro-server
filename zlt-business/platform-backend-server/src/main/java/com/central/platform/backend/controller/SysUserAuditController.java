package com.central.platform.backend.controller;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.user.feign.UserService;
import com.central.user.model.co.SysTansterMoneyPageCo;
import com.central.user.model.vo.SysUserAuditVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@Api(tags = "用户账变记录")
@RequestMapping("/sysUserAudit")
public class SysUserAuditController {


    @Resource
    private UserService userService;

    @ApiOperation(value = "稽核列表")
    @GetMapping("/users/list")
    public Result<PageResult<SysUserAuditVo>> list(@ModelAttribute SysTansterMoneyPageCo params) {
        PageResult<SysUserAuditVo> sysUserList = userService.findUserAuditList(params);
        return Result.succeed(sysUserList);
    }


}
