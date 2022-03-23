package com.central.business.feign.callback;

import com.central.business.feign.BusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 降级工场
 */
@Slf4j
public class BusinessServiceFallbackFactory implements FallbackFactory<BusinessService> {

    @Override
    public BusinessService create(Throwable cause) {
        return null;
    }
}
