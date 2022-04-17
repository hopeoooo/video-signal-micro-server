package com.central.user.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import com.central.user.dto.UserTansterMoneyDto;
import com.central.user.feign.callback.TansterMoneyServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 */
@FeignClient(name = ServiceNameConstants.USER_SERVICE, fallbackFactory = TansterMoneyServiceFallbackFactory.class, decode404 = true)
public interface TansterMoneyService {

    @GetMapping("/sysTansterMoney/findUserTansterMoneyDto")
    Result<List<UserTansterMoneyDto>> findUserTansterMoneyDto(@RequestParam Map<String, Object> params);
}
