package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;

import com.central.user.feign.UserService;
import com.central.user.model.co.UserWashCodeConfigCo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = "个人洗码配置")
@Slf4j
@RequestMapping("/userWashCode/backend")
public class UserWashCodeConfigController {

    @Resource
    private UserService userService;

    @ApiOperation("查询个人洗码配置")
    @ResponseBody
    @GetMapping("/userWashCode/findUserWashCodeConfigList/{userId}")
    public Result<List<UserWashCodeConfig>> findUserWashCodeConfigList(@PathVariable Long userId) {
        return userService.findUserWashCodeConfigList(userId);
    }


    @ApiOperation(value = "保存")
    @PostMapping("/userWashCode/saveUserWashCodeConfig")
    public Result saveUserWashCodeConfig(@RequestBody List<UserWashCodeConfigCo> list) {
        return userService.saveUserWashCodeConfig(list);
    }
}
