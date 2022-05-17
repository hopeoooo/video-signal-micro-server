package com.central.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.SysUserJetton;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.user.mapper.SysUserJettonMapper;
import com.central.user.service.ISysUserJettonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@CacheConfig(cacheNames = {"sysUserJetton"})
public class SysUserJettonServiceImp extends SuperServiceImpl<SysUserJettonMapper, SysUserJetton> implements ISysUserJettonService {

    @Override
    @Cacheable(key = "#userId")
    public SysUserJetton queryJettonByUid(Long userId) {
        LambdaQueryWrapper<SysUserJetton> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysUserJetton::getUid,userId);
        SysUserJetton sysUserJetton = baseMapper.selectOne(lqw);
        return sysUserJetton;
    }

    @Override
    @CachePut(key = "#userId")
    public SysUserJetton updateJettonConfig(String jettonConfig, Long userId) {
        SysUserJetton sysUserJetton = queryJettonByUid(userId);
        if (sysUserJetton == null) {
            sysUserJetton = new SysUserJetton();
            sysUserJetton.setUid(userId);
            sysUserJetton.setJettonConfig(jettonConfig);
            baseMapper.insert(sysUserJetton);
        } else {
            sysUserJetton.setJettonConfig(jettonConfig);
            baseMapper.updateById(sysUserJetton);
        }
        return sysUserJetton;
    }
}
