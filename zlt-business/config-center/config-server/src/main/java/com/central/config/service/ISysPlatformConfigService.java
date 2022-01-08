package com.central.config.service;

import com.central.common.model.Result;
import com.central.common.model.SysPlatformConfig;
import com.central.common.service.ISuperService;

import java.math.BigDecimal;

public interface ISysPlatformConfigService extends ISuperService<SysPlatformConfig> {
    /**
     * 列表
     * @return
     */
    SysPlatformConfig findTouristAmount();


    Result saveCache(BigDecimal touristAmount, BigDecimal touristSingleMaxBet);
}
