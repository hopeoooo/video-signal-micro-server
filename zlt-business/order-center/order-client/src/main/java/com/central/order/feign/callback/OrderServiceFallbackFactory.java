package com.central.order.feign.callback;

import com.central.common.model.Result;
import com.central.order.feign.OrderService;
import com.central.order.model.Orders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * orderService降级工场
 */
@Slf4j
public class OrderServiceFallbackFactory implements FallbackFactory<OrderService> {

    @Override
    public OrderService create(Throwable throwable) {
        return new OrderService() {
            @Override
            public Result save(Orders order) {
                log.error("订单保存失败:{}", order, throwable);
                return Result.failed("订单保存失败");
            }

            @Override
            public Result<Orders> findById(Long id) {
                log.error("根据ID订单查询失败:{}", id, throwable);
                return Result.failed("根据ID订单查询失败");
            }
        };
    }
}
