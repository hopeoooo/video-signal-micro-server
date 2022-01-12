package com.central.config.mapper;

import com.central.common.model.SysNotice;
import com.central.config.model.DownloadStation;
import com.central.db.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DownloadStationMapper extends SuperMapper<DownloadStation> {

}
