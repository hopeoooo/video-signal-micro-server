package com.central.translate.feign.callback;

import com.central.translate.feign.TranslateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * payService降级工场
 */
@Slf4j
public class TranslateServiceFallbackFactory implements FallbackFactory<TranslateService> {

    @Override
    public TranslateService create(Throwable cause) {
        return null;
    }
}
