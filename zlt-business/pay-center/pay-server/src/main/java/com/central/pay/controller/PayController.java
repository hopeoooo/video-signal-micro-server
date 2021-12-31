package com.central.pay.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付模块
 */
@Slf4j
@RestController
@Api(tags = "支付模块api")
@RequestMapping("/pay")
public class PayController {

    @ApiOperation(value = "查询支付列表")
    @GetMapping("/list")
    public String list(){
        return "test.game";
    }
}
