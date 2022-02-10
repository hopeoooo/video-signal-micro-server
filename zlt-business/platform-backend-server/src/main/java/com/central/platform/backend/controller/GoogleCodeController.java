package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.common.params.user.SysUserGoogleBindParams;
import com.central.common.params.user.SysUserParams;
import com.central.user.feign.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "后台绑定谷歌验证")
@RestController
@RequestMapping("/googleCode")
public class GoogleCodeController {

    @Resource
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/binding")
    @ApiOperation(value = "绑定")
    public Result<String> list(@ModelAttribute SysUserGoogleBindParams params) {
        return userService.bindGoogleCode(params);
    }

    @PostMapping("/getGoogleCodeLink")
    @ApiOperation(value = "得到谷歌二维码链接")
    public Result<String> getGoogleCodeLink(@ModelAttribute SysUserParams params) {
        return userService.getGoogleCodeLink(params);
    }
}
