package com.central.order.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 作者 owen E-mail: 624191343@qq.com
 * 用户
 */
@Slf4j
@RestController
@Api(tags = "订单模块api")
@RequestMapping("/order")
public class OrderController {

    @ApiOperation(value = "查询订单列表")
    @GetMapping("/list")
    public String list(){
        return "test.order";
    }
}
