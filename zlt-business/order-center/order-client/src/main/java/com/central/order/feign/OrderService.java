package com.central.order.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import com.central.order.feign.callback.OrderServiceFallbackFactory;
import com.central.order.model.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

;

/**
 * @author zlt
 */
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = OrderServiceFallbackFactory.class, decode404 = true)
public interface OrderService {

    @PostMapping("/orders/save")
    Result save(@RequestBody Order order);

    @GetMapping("/orders/findById/{id}")
    Result<Order> findById(@PathVariable("id") Long id);

}