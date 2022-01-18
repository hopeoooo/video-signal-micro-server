package com.central.user.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.SysTansterMoneyLog;
import com.central.db.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysTansterMoneyLogMapper extends SuperMapper<SysTansterMoneyLog> {

    List<SysTansterMoneyLog> findList(Page<SysTansterMoneyLog> page, @Param("u") Map<String, Object> params);
}
