package com.central.config.feign.callback;

import com.central.config.feign.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 降级工场
 */
@Slf4j
public class ConfigServiceFallbackFactory implements FallbackFactory<ConfigService> {

    @Override
    public ConfigService create(Throwable cause) {
        return null;
    }
}
