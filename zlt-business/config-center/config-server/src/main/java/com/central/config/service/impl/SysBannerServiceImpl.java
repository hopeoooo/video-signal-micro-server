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
import com.central.config.model.co.BannerCo;
import com.central.config.model.co.BannerUpdateStateCo;
import com.central.config.service.ISysBannerService;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
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
    public List<SysBanner> findBannerList(BannerCo params) {
        LambdaQueryWrapper<SysBanner> wrapper = new LambdaQueryWrapper<>();
        String languageType = params.getLanguageType();
        if (StringUtils.isNotBlank(languageType)) {
            wrapper.eq(SysBanner::getLanguageType, languageType);
        }
        wrapper.orderByAsc(SysBanner::getSort);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean delBannerId(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public int updateState(BannerUpdateStateCo params) {
        int i =0 ;
        Long id = params.getId();
        Boolean state = params.getState();
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
        //??????
        if (sysBanner.getId() == null) {
            insert = super.save(sysBanner);
        }else {
            //??????
            insert = super.updateById(sysBanner);
        }
        return insert ;
    }

    @Override
    public Integer queryTotal(Integer sort, String languageType) {
        LambdaQueryWrapper<SysBanner> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(languageType)) {
            wrapper.eq(SysBanner::getLanguageType, languageType);
        }
        if (sort!=null) {
            wrapper.eq(SysBanner::getSort, sort);
        }
        return baseMapper.selectCount(wrapper);
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
        LambdaQueryWrapper<SysBanner> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysBanner::getState, Boolean.TRUE);
        lqw.orderByAsc(SysBanner::getSort);
        lqw.orderByAsc(SysBanner::getLanguageType);
        List<SysBanner> bannerList = baseMapper.selectList(lqw);
        PushResult<List<SysBanner>> pushResult = PushResult.succeed(bannerList, SocketTypeConstant.BANNER,"?????????????????????");
        Result<String> push = pushService.sendAllMessage(JSONObject.toJSONString(pushResult));
        log.info("?????????????????????:{}",push);
    }
}