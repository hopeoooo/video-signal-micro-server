package com.central.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.central.common.model.Result;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.config.mapper.SysNoticeMapper;
import com.central.config.model.SysNotice;
import com.central.config.service.ISysNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@CacheConfig(cacheNames = {"sysNotice"})
public class SysNoticeServiceImpl extends SuperServiceImpl<SysNoticeMapper, SysNotice> implements ISysNoticeService {

    @Override
    public List<SysNotice> findNoticeList(Map<String, Object> params) {
        LambdaQueryWrapper<SysNotice> wrapper=new LambdaQueryWrapper<>();
        Integer type = MapUtils.getInteger(params, "type");
        Integer state = MapUtils.getInteger(params, "state");
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
    public Result updateEnabled(Map<String, Object> params) {
        Long id = MapUtils.getLong(params, "id");
        Boolean state = MapUtils.getBoolean(params, "state");
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


    public int selectCount(){
        LambdaQueryWrapper<SysNotice> wrapper=new LambdaQueryWrapper<>();
       return baseMapper.selectCount(wrapper);
    }
}