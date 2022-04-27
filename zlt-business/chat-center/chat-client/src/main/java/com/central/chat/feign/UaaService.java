package com.central.chat.feign;

import com.central.chat.feign.callback.UaaServiceFallbackFactory;
import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
@FeignClient(name = ServiceNameConstants.UAA_SERVICE, fallbackFactory = UaaServiceFallbackFactory.class, decode404 = true)
public interface UaaService {

    @GetMapping("/tokens/getUserInfoByToken")
    Result<SysUser> getUserInfoByToken(@RequestParam("token") String token);
}