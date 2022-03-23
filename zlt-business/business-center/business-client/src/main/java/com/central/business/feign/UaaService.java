package com.central.business.feign;

import com.central.business.feign.callback.UaaServiceFallbackFactory;
import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
@FeignClient(name = ServiceNameConstants.UAA_SERVICE, fallbackFactory = UaaServiceFallbackFactory.class, decode404 = true)
public interface UaaService {

    @PostMapping("/oauth/token")
    Result login(@RequestHeader("authorization") String authorization, @RequestParam("username") String username, @RequestParam("password") String password,
                 @RequestParam("grant_type") String grantType, @RequestParam("deviceId") String deviceId);
}