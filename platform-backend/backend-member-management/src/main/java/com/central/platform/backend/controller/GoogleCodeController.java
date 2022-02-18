package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.user.model.co.SysUserGoogleBindCoCo;
import com.central.user.model.co.SysUserParamsCo;
import com.central.user.feign.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Api(tags = "后台绑定谷歌验证")
@RestController
@RequestMapping("/member")
public class GoogleCodeController {

    @Resource
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/googleCode/binding")
    @ApiOperation(value = "绑定")
    public Result<String> list(@ModelAttribute SysUserGoogleBindCoCo params) {
        return userService.bindGoogleCode(params);
    }

    @PostMapping("/googleCode/getGoogleCodeLink")
    @ApiOperation(value = "得到谷歌二维码链接")
    public Result<String> getGoogleCodeLink(@ModelAttribute SysUserParamsCo params) {
        return userService.getGoogleCodeLink(params);
    }
}
