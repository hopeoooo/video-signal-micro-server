package com.central.translate.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.translate.feign.callback.TranslateServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 */
@FeignClient(name = ServiceNameConstants.TRANSLATE_SERVICE, fallbackFactory = TranslateServiceFallbackFactory.class, decode404 = true)
public interface TranslateService {
    /**
     * 查询游戏列表
     */
    @PostMapping(value = "/translate/test")
    String test();
}