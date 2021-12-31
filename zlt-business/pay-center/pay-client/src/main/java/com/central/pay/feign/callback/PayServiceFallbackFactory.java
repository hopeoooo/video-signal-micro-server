package com.central.pay.feign.callback;

import com.central.pay.feign.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * payService降级工场
 */
@Slf4j
public class PayServiceFallbackFactory implements FallbackFactory<PayService> {

    @Override
    public PayService create(Throwable cause) {
        return null;
    }
}
