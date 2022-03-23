package com.central.business.feign;

import com.central.business.feign.callback.BusinessServiceFallbackFactory;
import com.central.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;

/**
 */
@FeignClient(name = ServiceNameConstants.BUSINESS_SERVICE, fallbackFactory = BusinessServiceFallbackFactory.class, decode404 = true)
public interface BusinessService {
}