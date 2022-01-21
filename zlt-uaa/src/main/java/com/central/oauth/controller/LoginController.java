package com.central.oauth.controller;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登陆【参考接口】
 */
@Api(tags = "登陆【参考接口】")
@RestController
@RequestMapping("/test/oauth")
public class LoginController {

    /**
     * 游客登陆
     */
    @PostMapping("/appGuest")
    @ApiOperation(value = "游客登陆", notes = "post请求请求接口：/oauth/token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Username", value = "授权用户：video-signal-webapp", required = true, dataType = "String"),
            @ApiImplicitParam(name = "Password", value = "授权密码：video-signal-webapp_YYDS", required = true, dataType = "String"),
            @ApiImplicitParam(name = "grant_type", value = "guest", required = true, dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 100, message = "逻辑验证错误码 100~200" ),
            @ApiResponse(code = 101, message = "账号类型不能为空" ),
            @ApiResponse(code = 102, message = "请在请求参数中携带deviceId参数" ),
            @ApiResponse(code = 103, message = "请填写验证码" ),
            @ApiResponse(code = 104, message = "验证码不存在或已过期" ),
            @ApiResponse(code = 105, message = "验证码不正确" ),
            @ApiResponse(code = 106, message = "请在请求参数中携带googleCode参数" ),
            @ApiResponse(code = 107, message = "请在请求参数中携带username参数" ),
            @ApiResponse(code = 108, message = "用户名或密码错误" ),
            @ApiResponse(code = 109, message = "请先绑定谷歌身份验证器" ),
            @ApiResponse(code = 110, message = "谷歌身份验证失败" ),
            @ApiResponse(code = 111, message = "手机号或密码错误" ),
            @ApiResponse(code = 112, message = "openId错误" ),
            @ApiResponse(code = 130, message = "获取游客失败" ),
            @ApiResponse(code = 150, message = "获取用户信息失败" )
    })
    public void loginAppGuest(@PathVariable String Username, @PathVariable String Password, @PathVariable String grant_type) {
//        CodeErrorAuthEnum
    }

    /**
     * 视讯app登陆
     */
    @PostMapping("/app")
    @ApiOperation(value = "app登陆", notes = "post请求请求接口：/oauth/token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Username", value = "授权用户：video-signal-webapp", required = true, dataType = "String"),
            @ApiImplicitParam(name = "Password", value = "授权密码：video-signal-webapp_YYDS", required = true, dataType = "String"),
            @ApiImplicitParam(name = "grant_type", value = "password_code", required = true, dataType = "String"),
            @ApiImplicitParam(name = "username", value = "用户账号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "用户密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "deviceId", value = "UUID随机值", required = true, dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 100, message = "逻辑验证错误码 100~200" ),
            @ApiResponse(code = 101, message = "账号类型不能为空" ),
            @ApiResponse(code = 102, message = "请在请求参数中携带deviceId参数" ),
            @ApiResponse(code = 103, message = "请填写验证码" ),
            @ApiResponse(code = 104, message = "验证码不存在或已过期" ),
            @ApiResponse(code = 105, message = "验证码不正确" ),
            @ApiResponse(code = 106, message = "请在请求参数中携带googleCode参数" ),
            @ApiResponse(code = 107, message = "请在请求参数中携带username参数" ),
            @ApiResponse(code = 108, message = "用户名或密码错误" ),
            @ApiResponse(code = 109, message = "请先绑定谷歌身份验证器" ),
            @ApiResponse(code = 110, message = "谷歌身份验证失败" ),
            @ApiResponse(code = 111, message = "手机号或密码错误" ),
            @ApiResponse(code = 112, message = "openId错误" ),
            @ApiResponse(code = 130, message = "获取游客失败" ),
            @ApiResponse(code = 150, message = "获取用户信息失败" )
    })
    public void loginApp(@PathVariable String Username, @PathVariable String Password, @PathVariable String grant_type,
            @PathVariable String username, @PathVariable String password, @PathVariable String deviceId) {
    }


    /**
     * 视讯后台管理系统登陆登陆
     */
    @PostMapping("/backend")
    @ApiOperation(value = "后台管理系统登陆", notes = "post请求请求接口：/oauth/token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Username", value = "授权用户：video-signal-platform", required = true, dataType = "String"),
            @ApiImplicitParam(name = "Password", value = "授权密码：video-signal-platform_YYDS", required = true, dataType = "String"),
            @ApiImplicitParam(name = "grant_type", value = "password_google", required = true, dataType = "String"),
            @ApiImplicitParam(name = "username", value = "用户账号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "googleCode", value = "google验证码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "deviceId", value = "UUID随机值", required = true, dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 100, message = "逻辑验证错误码 100~200" ),
            @ApiResponse(code = 101, message = "账号类型不能为空" ),
            @ApiResponse(code = 102, message = "请在请求参数中携带deviceId参数" ),
            @ApiResponse(code = 103, message = "请填写验证码" ),
            @ApiResponse(code = 104, message = "验证码不存在或已过期" ),
            @ApiResponse(code = 105, message = "验证码不正确" ),
            @ApiResponse(code = 106, message = "请在请求参数中携带googleCode参数" ),
            @ApiResponse(code = 107, message = "请在请求参数中携带username参数" ),
            @ApiResponse(code = 108, message = "用户名或密码错误" ),
            @ApiResponse(code = 109, message = "请先绑定谷歌身份验证器" ),
            @ApiResponse(code = 110, message = "谷歌身份验证失败" ),
            @ApiResponse(code = 111, message = "手机号或密码错误" ),
            @ApiResponse(code = 112, message = "openId错误" ),
            @ApiResponse(code = 130, message = "获取游客失败" ),
            @ApiResponse(code = 150, message = "获取用户信息失败" )
    })
    public void loginPlatformBackend(@PathVariable String Username, @PathVariable String Password, @PathVariable String grant_type,
                      @PathVariable String username, @PathVariable String googleCode, @PathVariable String deviceId) {
    }
}