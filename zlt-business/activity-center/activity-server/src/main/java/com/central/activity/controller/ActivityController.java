package com.central.activity.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 活动模块
 */
@Slf4j
@RestController
@Api(tags = "活动模块api")
@RequestMapping("/activity")
public class ActivityController {

    @ApiOperation(value = "查询活动列表")
    @GetMapping("/list")
    public String list(){
        return "test.game";
    }
}
