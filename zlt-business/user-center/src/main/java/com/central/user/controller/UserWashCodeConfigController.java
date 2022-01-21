package com.central.user.controller;

import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;
import com.central.user.service.IUserWashCodeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@Api(tags = "个人洗码配置")
@RequestMapping("/userWashCode")
public class UserWashCodeConfigController {


    @Autowired
    private IUserWashCodeConfigService userWashCodeConfigService;


    @ApiOperation("查询个人洗码配置")
    @ResponseBody
    @GetMapping("/findUserWashCodeConfigList/{userId}")
    public Result<UserWashCodeConfig> findUserWashCodeConfigList(@PathVariable Long userId) {
        UserWashCodeConfig userWashCodeConfig = userWashCodeConfigService.findUserWashCodeConfigList(userId);
        return Result.succeed(userWashCodeConfig);
    }



    @ApiOperation(value = "保存")
    @PostMapping("/saveUserWashCodeConfig")
    public Result saveUserWashCodeConfig(@RequestBody UserWashCodeConfig userWashCodeConfig) {
        return userWashCodeConfigService.saveCache(userWashCodeConfig);
    }
}
