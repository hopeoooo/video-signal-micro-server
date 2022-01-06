package com.central.order.controller;

import com.central.common.model.PageResult2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import com.central.order.model.Order;
import com.central.order.service.IOrderService;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SuperPage;

/**
 * 订单表
 *
 * @author zlt
 * @date 2022-01-03 18:27:12
 */
@Slf4j
@RestController
@RequestMapping("/orders")
@Api(tags = "订单表")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @ApiOperation(value = "查询列表")
    @GetMapping("/list")
    public Result<PageResult2<Order>> list(@ModelAttribute SuperPage superPage) {
        return Result.succeed(orderService.findList(superPage));
    }

    @ApiOperation(value = "根据ID查询")
    @GetMapping("/findById/{id}")
    public Result<Order> findById(@PathVariable Long id) {
        Order model = orderService.getById(id);
        return Result.succeed(model);
    }

    @ApiOperation(value = "保存")
    @PostMapping("/save")
    public Result save(@RequestBody Order order) {
        orderService.saveOrUpdate(order);
        return Result.succeed();
    }

    @ApiOperation(value = "根据ID删除")
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Long id) {
        orderService.removeById(id);
        return Result.succeed();
    }
}
