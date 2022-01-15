package com.central.config.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.PageResult2;
import com.central.common.model.Result;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.config.mapper.DownloadStationMapper;
import com.central.config.model.DownloadStation;
import com.central.config.service.IDownloadStationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@CacheConfig(cacheNames = {"downloadStation"})
public class DownloadStationServiceImpl extends SuperServiceImpl<DownloadStationMapper, DownloadStation> implements IDownloadStationService {

    @Override
    public PageResult2<DownloadStation> findDownloadStationList(Map<String, Object> map) {
        Page<DownloadStation> page = new Page<>(MapUtils.getInteger(map, "page"),  MapUtils.getInteger(map, "limit"));
        List<DownloadStation> list = baseMapper.selectList(page);
        return PageResult2.<DownloadStation>builder().data(list).count(page.getTotal()).build();
    }

    @Override
    public Result saveOrUpdateDownloadStation(DownloadStation downloadStation) throws Exception {
        boolean insert =false;
        if (downloadStation.getId()==null){
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