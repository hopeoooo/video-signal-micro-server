package com.central.config.service.impl;

import com.central.common.model.Result;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.config.mapper.SysPlatformMapper;
import com.central.config.model.SysPlatformConfig;
import com.central.config.service.ISysPlatformConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
@CacheConfig(cacheNames = {"sysPlatformConfig"})
public class SysPlatformConfigServiceImpl extends SuperServiceImpl<SysPlatformMapper, SysPlatformConfig> implements ISysPlatformConfigService {


    @Resource
    private ISysPlatformConfigService platformConfigService;


    @Override
    public SysPlatformConfig findTouristAmount() {
        List<SysPlatformConfig> list = platformConfigService.list();
        return list!=null && list.size()>0?list.get(0):new SysPlatformConfig();
    }

    @Override
    public Result saveCache(BigDecimal touristAmount, BigDecimal touristSingleMaxBet) {
        SysPlatformConfig touristAmountInfo = findTouristAmount();
        touristAmountInfo.setTouristAmount(touristAmount);
        touristAmountInfo.setTouristSingleMaxBet(touristSingleMaxBet);
        boolean i =platformConfigService.saveOrUpdate(touristAmountInfo);
        return i  ? Result.succeed("更新成功") : Result.failed("更新失败");
    }

    @Override
    public Result saveBetMultiple(BigDecimal betMultiple, BigDecimal betZrrorPint) {
        SysPlatformConfig touristAmountInfo = findTouristAmount();
        touristAmountInfo.setBetMultiple(betMultiple);
        touristAmountInfo.setBetZrrorPint(betZrrorPint);
        boolean i =platformConfigService.saveOrUpdate(touristAmountInfo);
        return i  ? Result.succeed("更新成功") : Result.failed("更新失败");
    }
}