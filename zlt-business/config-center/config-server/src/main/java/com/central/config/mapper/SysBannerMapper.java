package com.central.config.mapper;

import com.central.config.model.SysBanner;
import com.central.db.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysBannerMapper extends SuperMapper<SysBanner> {

    Integer queryTotal(Integer sort);

}
