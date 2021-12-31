package com.central.risk.management.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.risk.management.feign.callback.RiskManagementServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

;

/**
 * @author zlt
 */
@FeignClient(name = ServiceNameConstants.RISK_MANAGEMENT_SERVICE, fallbackFactory = RiskManagementServiceFallbackFactory.class, decode404 = true)
public interface RiskManagementService {
    /**
     * 查询游戏列表
     */
    @PostMapping(value = "/risk/management/list")
    String list();
}