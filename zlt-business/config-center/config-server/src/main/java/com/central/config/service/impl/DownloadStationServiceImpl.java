package com.central.config.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.co.PageCo;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.config.mapper.DownloadStationMapper;
import com.central.config.model.DownloadStation;
import com.central.config.service.IDownloadStationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@CacheConfig(cacheNames = {"downloadStation"})
public class DownloadStationServiceImpl extends SuperServiceImpl<DownloadStationMapper, DownloadStation> implements IDownloadStationService {

    @Override
    public PageResult<DownloadStation> findDownloadStationList(PageCo map) {
        Page<DownloadStation> page = new Page<>(map.getPage(),  map.getLimit());
        List<DownloadStation> list = baseMapper.findList(page);
        return PageResult.<DownloadStation>builder().data(list).count(page.getTotal()).build();
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


    /**
     * 根据id查询公告
     * @param id
     * @return
     */
    @Override
    public DownloadStation selectById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 生成版本号
     * @param terminalType
     * @return
     */
    @Override
    public List<String> generateVersionNumber(String terminalType){
        List<String> list=new ArrayList<>();
        String versionNumber = baseMapper.getVersionNumber(terminalType);
        if (versionNumber==null){
            versionNumber="1.1.1";
        }
        list.add(versionNumber);
        for (int j=0;j<9;j++){
            String[] aa =versionNumber.split("\\.");
            Integer one=null;
            Integer two=null;
            Integer three=null;
            for (int i = 0; i < aa.length; i++) {
                if (i == 0) {
                    one = Integer.valueOf(aa[i]);
                }
                if (i == 1) {
                    two = Integer.valueOf(aa[i]);
                }
                if (i == 2) {
                    three = Integer.valueOf(aa[i]);
                }
            }
            if (three<9){
                three+=1;
            }else {
                three=0;
                if (two<9){
                    two+=1;
                }else {
                    two=0;
                    one+=1;
                }
            }
            versionNumber=one+"."+two+"."+three;
            list.add(versionNumber);
        }
        return list;
    }
}