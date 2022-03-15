package com.central.config.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.PushResult;
import com.central.common.model.Result;
import com.central.common.service.impl.SuperServiceImpl;
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
        if (type!=null){
            wrapper.eq(SysNotice::getType, type);
        }
        if (state!=null) {
            wrapper.eq(SysNotice::getState, state);
        }
        return  baseMapper.selectList(wrapper);
    }

    /**
     * 根据id查询公告
     * @param id
     * @return
     */
    @Override
    public SysNotice selectById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 删除公告
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
            return Result.failed("此公告不存在");
        }
        sysNotice.setState(state);
        int i = baseMapper.updateById(sysNotice);
        return i > 0 ? Result.succeed(sysNotice, "更新成功") : Result.failed("更新失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveOrUpdateUser(SysNotice notice) throws Exception {
        boolean insert =false;
        //新增
        if (notice.getId() == null) {
            int i = selectCount();
            if (i==20){
                return Result.failed("最多添加20条数据");
            }
            insert = super.save(notice);
        }else {
            SysNotice sysNotice = baseMapper.selectById(notice.getId());
            if (sysNotice == null) {
                return Result.failed("此公告不存在");
            }
            //修改
            insert = super.updateById(notice);
        }
        return insert ? Result.succeed(notice, "操作成功") : Result.failed("操作失败");
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
        List<SysNotice> noticeList = getNoticeList();
        PushResult<List<SysNotice>> pushResult = PushResult.succeed(noticeList, SocketTypeConstant.NOTICE,"公告推送成功");
        Result<String> push = pushService.sendAllMessage(JSONObject.toJSONString(pushResult));
        log.info("公告消息推送结果:{}",push);
    }

    public int selectCount(){
        LambdaQueryWrapper<SysNotice> wrapper=new LambdaQueryWrapper<>();
       return baseMapper.selectCount(wrapper);
    }
}