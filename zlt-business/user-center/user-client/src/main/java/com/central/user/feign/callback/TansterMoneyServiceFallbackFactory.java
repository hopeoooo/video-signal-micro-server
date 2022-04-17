package com.central.user.feign.callback;

import com.central.common.model.Result;
import com.central.user.dto.UserTansterMoneyDto;
import com.central.user.feign.TansterMoneyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TansterMoneyServiceFallbackFactory implements FallbackFactory<TansterMoneyService> {
    @Override
    public TansterMoneyService create(Throwable cause) {
        return new TansterMoneyService() {
            @Override
            public Result<List<UserTansterMoneyDto>> findUserTansterMoneyDto(Map<String, Object> params) {
                log.error("服务器异常, findUserTansterMoneyDto查询会员充值报表失败: {}", params);
                return Result.failed("查询会员充值报表失败");
            }
        };
    }
}
