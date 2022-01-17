package com.central.config.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.dto.LoginLogPageDto;
import com.central.config.model.DownloadStation;
import com.central.db.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DownloadStationMapper extends SuperMapper<DownloadStation> {


    List<DownloadStation> selectList(Page<DownloadStation> page);

}
