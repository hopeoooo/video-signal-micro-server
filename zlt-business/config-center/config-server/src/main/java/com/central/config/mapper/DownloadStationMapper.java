package com.central.config.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.config.model.DownloadStation;
import com.central.db.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DownloadStationMapper extends SuperMapper<DownloadStation> {


    List<DownloadStation> findList(Page<DownloadStation> page);

    String  getVersionNumber(String terminalType);

}
