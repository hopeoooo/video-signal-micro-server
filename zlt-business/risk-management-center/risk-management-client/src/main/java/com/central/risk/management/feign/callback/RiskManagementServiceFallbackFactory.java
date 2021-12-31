package com.central.risk.management.feign.callback;

import com.central.risk.management.feign.RiskManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * gameService降级工场
 */
@Slf4j
public class RiskManagementServiceFallbackFactory implements FallbackFactory<RiskManagementService> {

    @Override
    public RiskManagementService create(Throwable cause) {
        return null;
    }
}
