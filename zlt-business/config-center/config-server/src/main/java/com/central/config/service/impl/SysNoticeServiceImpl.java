package com.central.config.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.PushResult;
import com.central.common.model.Result;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.common.utils.StringUtils;
import com.central.config.mapper.SysNoticeMapper;
import com.central.config.model.SysNotice;
import com.central.config.model.co.FindNoticeCo;
import com.central.config.model.co.UpdateNoticeCo;
import com.central.config.service.ISysNoticeService;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@CacheConfig(cacheNames = {"sysNotice"})
public class SysNoticeServiceImpl extends SuperServiceImpl<SysNoticeMapper, SysNotice> implements ISysNoticeService {

    @Autowired
    private PushService pushService;

    @Override
    public List<SysNotice> findNoticeList(FindNoticeCo params) {
        LambdaQueryWrapper<SysNotice> wrapper=new LambdaQueryWrapper<>();
        Integer type = params.getType();
        Integer state = params.getState();
        String languageType = params.getLanguageType();
        if (type!=null){
            wrapper.eq(SysNotice::getType, type);
        }
        if (state!=null) {
            wrapper.eq(SysNotice::getState, state);
        }
        if (StringUtils.isNotBlank(languageType)) {
            wrapper.eq(SysNotice::getLanguageType, languageType);
        }
        return  baseMapper.selectList(wrapper);
    }

    /**
     * ??????id????????????
     * @param id
     * @return
     */
    @Override
    public SysNotice selectById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * ????????????
     * @param id
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delNoticeId(Long id) {
        return baseMapper.deleteById(id) > 0;
    }


    @Override
    public Result updateEnabled(UpdateNoticeCo params) {
        Long id = params.getId();
        Boolean state = params.getState();
        SysNotice sysNotice = baseMapper.selectById(id);
        if (sysNotice == null) {
            return Result.failed("??????????????????");
        }
        sysNotice.setState(state);
        int i = baseMapper.updateById(sysNotice);
        return i > 0 ? Result.succeed(sysNotice, "????????????") : Result.failed("????????????");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveOrUpdateUser(SysNotice notice) throws Exception {
        boolean insert =false;
        //??????
        if (notice.getId() == null) {
            int i = selectCount();
            if (i==20){
                return Result.failed("????????????20?????????");
            }
            insert = super.save(notice);
        }else {
            SysNotice sysNotice = baseMapper.selectById(notice.getId());
            if (sysNotice == null) {
                return Result.failed("??????????????????");
            }
            //??????
            insert = super.updateById(notice);
        }
        return insert ? Result.succeed(notice, "????????????") : Result.failed("????????????");
    }

    @Override
    public List<SysNotice> getNoticeList() {
        LambdaQueryWrapper<SysNotice> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysNotice::getState, Boolean.TRUE);
        lqw.orderByDesc(SysNotice::getCreateTime);
        List<SysNotice> noticeList = baseMapper.selectList(lqw);
        return noticeList;
    }

    @Override
    @Async
    public void syncPushNoticeToWebApp() {
        LambdaQueryWrapper<SysNotice> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysNotice::getState, Boolean.TRUE);
        lqw.orderByDesc(SysNotice::getCreateTime);
        lqw.orderByDesc(SysNotice::getLanguageType);
        List<SysNotice> noticeList = baseMapper.selectList(lqw);
        PushResult<List<SysNotice>> pushResult = PushResult.succeed(noticeList, SocketTypeConstant.NOTICE,"??????????????????");
        Result<String> push = pushService.sendAllMessage(JSONObject.toJSONString(pushResult));
        log.info("????????????????????????:{}",push);
    }

    public int selectCount(){
        LambdaQueryWrapper<SysNotice> wrapper=new LambdaQueryWrapper<>();
       return baseMapper.selectCount(wrapper);
    }
}