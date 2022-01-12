package com.central.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.central.common.model.Result;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.config.mapper.DownloadStationMapper;
import com.central.config.model.DownloadStation;
import com.central.config.service.IDownloadStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@CacheConfig(cacheNames = {"downloadStation"})
public class DownloadStationServiceImpl extends SuperServiceImpl<DownloadStationMapper, DownloadStation> implements IDownloadStationService {
    @Override
    public List<DownloadStation> findDownloadStationList() {
        LambdaQueryWrapper<DownloadStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DownloadStation::getUpdateTime);
        return   baseMapper.selectList(wrapper);
    }

    @Override
    public Result saveOrUpdateDownloadStation(DownloadStation downloadStation) throws Exception {
        boolean insert =false;
        if (downloadStation.getId()==null){
            LambdaQueryWrapper<DownloadStation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DownloadStation::getTerminalType,downloadStation.getTerminalType());
            DownloadStation downloadStation1 = baseMapper.selectOne(wrapper);
            if (downloadStation1!=null){
                return Result.failed("不允许添加相同的终端类型");
            }
            insert= super.save(downloadStation);
        }else {
            DownloadStation info = baseMapper.selectById(downloadStation.getId());
            if(info==null){
                return Result.failed("此数据不存在");
            }
            insert = super.updateById(downloadStation);
        }
        return insert ? Result.succeed(downloadStation, "操作成功") : Result.failed("操作失败");
    }
}