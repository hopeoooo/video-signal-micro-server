package com.central.platform.backend.user.controller;

import com.central.common.feign.UserService;
import com.central.common.model.LoginAppUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Api(tags = "test1")
@Slf4j
@RequestMapping("/test")
public class SysUserController {

    @Resource
    UserService userService;

    /**
     * 测试
     */
    @ApiOperation(value = "测试")
    @GetMapping("/test")
    public String test() {
        LoginAppUser loginAppUser =  userService.findByMobile("admin");
        return loginAppUser.getUserId();
    }
}
