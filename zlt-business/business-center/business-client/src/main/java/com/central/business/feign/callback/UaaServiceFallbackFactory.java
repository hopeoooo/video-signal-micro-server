package com.central.business.feign.callback;

import com.central.business.feign.UaaService;
import com.central.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 降级工场
 */
@Slf4j
public class UaaServiceFallbackFactory implements FallbackFactory<UaaService> {

    @Override
    public UaaService create(Throwable cause) {
        return new UaaService() {
            @Override
            public Result login(String authorization, String userName, String password, String grantType, String deviceId) {
                log.error("登录失败:{}", userName);
                return Result.failed("登录失败");
            }
        };
    }
}
