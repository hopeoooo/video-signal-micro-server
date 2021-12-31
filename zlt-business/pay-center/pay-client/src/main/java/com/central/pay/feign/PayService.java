package com.central.pay.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.pay.feign.callback.PayServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 */
@FeignClient(name = ServiceNameConstants.PAY_SERVICE, fallbackFactory = PayServiceFallbackFactory.class, decode404 = true)
public interface PayService {
    /**
     * 查询游戏列表
     */
    @PostMapping(value = "/pay/list")
    String list();
}