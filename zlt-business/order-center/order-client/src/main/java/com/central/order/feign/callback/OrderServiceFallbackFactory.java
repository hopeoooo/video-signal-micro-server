package com.central.order.feign.callback;

import com.central.order.feign.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * orderService降级工场
 */
@Slf4j
public class OrderServiceFallbackFactory implements FallbackFactory<OrderService> {

    @Override
    public OrderService create(Throwable cause) {
        return null;
    }
}
