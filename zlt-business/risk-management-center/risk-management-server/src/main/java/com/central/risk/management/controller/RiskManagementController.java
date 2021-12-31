package com.central.risk.management.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 风控模块
 */
@Slf4j
@RestController
@Api(tags = "风控模块api")
@RequestMapping("/risk/management")
public class RiskManagementController {

    @ApiOperation(value = "查询风控列表")
    @GetMapping("/list")
    public String list(){
        return "test.game";
    }
}
