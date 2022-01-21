package com.central.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.config.mapper.WashCodeConfigMapper;
import com.central.config.model.WashCodeConfig;
import com.central.config.service.IWashCodeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@CacheConfig(cacheNames = {"washCodeConfig"})
public class WashCodeConfigServiceImpl extends SuperServiceImpl<WashCodeConfigMapper, WashCodeConfig> implements IWashCodeConfigService {


    @Override
    public List<WashCodeConfig> findWashCodeConfigList() {
        LambdaQueryWrapper<WashCodeConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(WashCodeConfig::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

}
