package com.central.agent.feign;

import com.central.agent.feign.callback.AgentServiceFallbackFactory;
import com.central.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

;

/**
 */
@FeignClient(name = ServiceNameConstants.AGENT_SERVICE, fallbackFactory = AgentServiceFallbackFactory.class, decode404 = true)
public interface AgentService {
    /**
     * 查询代理列表
     */
    @PostMapping(value = "/game/list")
    String list();
}