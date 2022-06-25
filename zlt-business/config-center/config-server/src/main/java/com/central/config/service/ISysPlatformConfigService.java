package com.central.config.service;

import com.central.common.model.Result;
import com.central.common.service.ISuperService;
import com.central.config.model.SysPlatformConfig;

import java.math.BigDecimal;

public interface ISysPlatformConfigService extends ISuperService<SysPlatformConfig> {
    /**
     * 列表
     * @return
     */
    SysPlatformConfig findTouristAmount();


    Result saveCache(BigDecimal touristAmount, BigDecimal touristSingleMaxBet);

    Result saveBetMultiple(BigDecimal betMultiple, BigDecimal betZrrorPint);
}
