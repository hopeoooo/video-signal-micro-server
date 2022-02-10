package com.central.platform.backend.controller;

import com.central.common.model.LoginAppUser;
import com.central.common.model.Result;
import com.central.common.utils.GoogleAuthUtil;
import com.central.common.utils.PwdEncoderUtil;
import com.central.user.feign.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "后台绑定谷歌验证")
@RestController
@RequestMapping("/googleCode")
public class GoogleCodeController {

    @Resource
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/binding")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "管理员账号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "管理员密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "googleCode", value = "验证码", required = true, dataType = "String")
    })
    @ApiOperation(value = "绑定")
    public Result list(@RequestParam Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String googleCode = params.get("googleCode");
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(googleCode)){
            return Result.failed("参数必填");
        }
        LoginAppUser loginAppUser = userService.findByUsername(username);
        if (loginAppUser == null || !loginAppUser.getType().equals("BACKEND")) {
            return Result.failed("用户名或密码错误");
        }
        if (loginAppUser.getGaBind() != null && loginAppUser.getGaBind() == 1) {
            return Result.failed("该账号已经绑定谷歌验证码");
        }
        if (StringUtils.isBlank(loginAppUser.getGaKey())) {
            return Result.failed("请先绑定谷歌身份验证器");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("id",loginAppUser.getId());
        param.put("gaBind",1);
        Result result = userService.updateGaBind(param);
        if (result != null && result.getResp_code() == 0){
            return Result.succeed();
        }
        return Result.failed("绑定失败");
    }

    @PostMapping("/getGoogleCodeLink")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "管理员账号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "管理员密码", required = true, dataType = "String")
    })
    @ApiOperation(value = "得到谷歌二维码链接")
    public Result getGoogleCodeLink(@RequestParam Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            return Result.failed("参数必填");
        }
        LoginAppUser loginAppUser = userService.findByUsername(username);
        if (loginAppUser == null || !loginAppUser.getType().equals("BACKEND")) {
            return Result.failed("用户名或密码错误");
        }
        if (!loginAppUser.isEnabled()){
            return Result.failed("该账号状态异常");
        }
        if (loginAppUser.getGaBind() != null && loginAppUser.getGaBind() == 1) {
            return Result.failed("该账号已经绑定谷歌验证码");
        }
        PasswordEncoder encoder = PwdEncoderUtil.getDelegatingPasswordEncoder("bcrypt");
        Boolean match = encoder.matches(password,loginAppUser.getPassword());
        if (!match){
            return Result.failed("用户名或密码错误");
        }
//        if (!passwordEncoder.matches(password, loginAppUser.getPassword())) {
//            return Result.failed("用户名或密码错误");
//        }
        String secret = GoogleAuthUtil.generateSecretKey();
        Map<String, Object> param = new HashMap<>();
        param.put("id",loginAppUser.getId());
        param.put("gaKey",secret);
        Result result = userService.updateGaKey(param);
        if (result != null && result.getResp_code() == 0){
            String qrcode = GoogleAuthUtil.getQcode(username, secret);
            return Result.succeed(qrcode,"");
        }
        return Result.failed("获取谷歌二维码失败");
    }
}
