package com.central.user.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.central.common.model.Result;
import com.central.common.model.SysPlatformConfig;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.user.mapper.SysPlatformConfigMapper;
import com.central.user.service.ISysPlatformConfigService;
import com.central.user.service.ISysRoleMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
@CacheConfig(cacheNames = {"sysPlatformConfig"})
public class SysPlatformConfigServiceImpl extends SuperServiceImpl<SysPlatformConfigMapper, SysPlatformConfig> implements ISysPlatformConfigService {


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
}