package com.central.order.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.order.feign.callback.OrderServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

;

/**
 * @author zlt
 */
@FeignClient(name = ServiceNameConstants.GAME_SERVICE, fallbackFactory = OrderServiceFallbackFactory.class, decode404 = true)
public interface OrderService {
    /**
     * 查询游戏列表
     */
    @PostMapping(value = "/game/list")
    String list();
}