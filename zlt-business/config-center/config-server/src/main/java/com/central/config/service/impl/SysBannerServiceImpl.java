package com.central.config.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.PushResult;
import com.central.common.model.Result;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.config.mapper.SysBannerMapper;
import com.central.config.model.DownloadStation;
import com.central.config.model.SysBanner;
import com.central.config.model.SysNotice;
import com.central.config.service.ISysBannerService;
import com.central.push.feign.PushService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
@CacheConfig(cacheNames = {"sysBanner"})
public class SysBannerServiceImpl extends SuperServiceImpl<SysBannerMapper, SysBanner> implements ISysBannerService {

    @Autowired
    private PushService pushService;

    @Override
    public List<SysBanner> findBannerList() {
        LambdaQueryWrapper<SysBanner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysBanner::getSort);
        return baseMapper.selectList(wrapper);
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
            insert = super.save(sysBanner);
        }else {
            SysBanner banner = baseMapper.selectById(sysBanner.getId());
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

    @Override
    public List<SysBanner> getBannerList() {
        LambdaQueryWrapper<SysBanner> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysBanner::getState, Boolean.TRUE);
        lqw.orderByAsc(SysBanner::getSort);
        List<SysBanner> bannerList = baseMapper.selectList(lqw);
        return bannerList;
    }

    @Override
    @Async
    public void syncPushBannerToWebApp() {
        List<SysBanner> bannerList = getBannerList();
        PushResult<List<SysBanner>> pushResult = PushResult.succeed(bannerList, "banner");
        Result<String> push = pushService.push(JSONObject.toJSONString(pushResult));
        log.info("轮播图推送结果:{}",push);
    }
}