package com.central.config.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.config.feign.callback.ConfigServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

;

/**
 */
@FeignClient(name = ServiceNameConstants.CONFIG_SERVICE, fallbackFactory = ConfigServiceFallbackFactory.class, decode404 = true)
public interface ConfigService {
    /**
     * 查询游戏列表
     */
    @PostMapping(value = "/config/list")
    String list();

}