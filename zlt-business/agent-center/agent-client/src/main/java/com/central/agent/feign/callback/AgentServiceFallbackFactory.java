package com.central.agent.feign.callback;

import com.central.agent.feign.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 降级工场
 */
@Slf4j
public class AgentServiceFallbackFactory implements FallbackFactory<AgentService> {

    @Override
    public AgentService create(Throwable cause) {
        return null;
    }
}
