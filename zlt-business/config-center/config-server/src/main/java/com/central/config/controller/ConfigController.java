package com.central.config.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置模块
 */
@Slf4j
@RestController
@Api(tags = "配置模块api")
@RequestMapping("/config")
public class ConfigController {

    @ApiOperation(value = "查询配置列表")
    @GetMapping("/list")
    public String list(){
        return "test.game";
    }
}
