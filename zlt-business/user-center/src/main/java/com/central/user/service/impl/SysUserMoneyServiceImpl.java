package com.central.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.SysUserMoney;
import com.central.user.mapper.SysUserMoneyMapper;
import com.central.user.service.ISysUserMoneyService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.central.common.model.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.service.impl.SuperServiceImpl;

import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import lombok.extern.slf4j.Slf4j;


/**
 * 用户钱包表
 *
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
@Slf4j
@Service
@CacheConfig(cacheNames = {"sysUserMoney"})
public class SysUserMoneyServiceImpl extends SuperServiceImpl<SysUserMoneyMapper, SysUserMoney> implements ISysUserMoneyService {
    /**
     * 列表
     * @param params
     * @return
     */
    @Override
    public PageResult<SysUserMoney> findList(Map<String, Object> params){
        Page<SysUserMoney> page = new Page<>(MapUtils.getInteger(params, "page"), MapUtils.getInteger(params, "limit"));
        List<SysUserMoney> list  =  baseMapper.findList(page, params);
        return PageResult.<SysUserMoney>builder().data(list).code(0).count(page.getTotal()).build();
    }

    @Override
    @Cacheable(key = "#userId")
    public SysUserMoney findByUserId(Long userId) {
        LambdaQueryWrapper<SysUserMoney> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysUserMoney::getUserId, userId);
        return baseMapper.selectOne(lqw);
    }

    @Override
    @CachePut(key="#sysUserMoney.userId")
    public SysUserMoney saveCache(SysUserMoney sysUserMoney) {
        baseMapper.insert(sysUserMoney);
        return sysUserMoney;
    }
}
