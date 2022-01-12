package com.central.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.config.mapper.SysBannerMapper;
import com.central.config.model.SysBanner;
import com.central.config.service.ISysBannerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@CacheConfig(cacheNames = {"sysBanner"})
public class SysBannerServiceImpl extends SuperServiceImpl<SysBannerMapper, SysBanner> implements ISysBannerService {

    @Override
    public List<SysBanner> findBannerList() {
        return  baseMapper.selectList( new QueryWrapper<SysBanner>().orderByAsc("sort"));
    }
    @Override
    public int updateEndTimeState() {
        LambdaQueryWrapper<SysBanner> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SysBanner::getEndTime, new Date());
        SysBanner banner = new SysBanner();
        banner.setState(false);
        return baseMapper.update(banner, wrapper);
    }

    @Override
    public boolean delBannerId(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public int updateState(Map<String, Object> params) {
        int i =0 ;
        Long id = MapUtils.getLong(params, "id");
        Boolean state = MapUtils.getBoolean(params, "state");
        SysBanner banner = baseMapper.selectById(id);
        if (banner == null) {
            return i;
        }
        banner.setState(state);
        i = baseMapper.updateById(banner);
        return i ;
    }

    @Override
    public SysBanner selectById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public boolean saveOrUpdateUser(SysBanner sysBanner) throws Exception {
        boolean insert =false;
        //新增
        if (sysBanner.getId() == null) {
            //即时
            if(sysBanner.getStartMode()==0){
                sysBanner.setStartTime(new Date());
            }
            insert = super.save(sysBanner);
        }else {
            SysBanner banner = baseMapper.selectById(sysBanner.getId());
            if(sysBanner.getStartMode()==0 && banner.getStartMode()!=0){
                sysBanner.setStartTime(new Date());
            }
            if (banner == null) {
                return insert;
            }
            //修改
            insert = super.updateById(sysBanner);
        }
        return insert ;
    }

    @Override
    public Integer queryTotal(Integer sort) {
        return baseMapper.queryTotal(sort);
    }
}