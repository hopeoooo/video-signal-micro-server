package com.central.chat.feign.callback;

import com.central.chat.feign.UaaService;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
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
            public Result<SysUser> getUserInfoByToken(String token) {
                log.error("getUserInfoByToken根据token查询用户信息失败:{}", token);
                return Result.failed("查询用户信息失败");
            }
        };
    }
}
