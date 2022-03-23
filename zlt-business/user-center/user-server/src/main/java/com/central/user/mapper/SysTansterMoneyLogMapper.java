package com.central.user.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.PageResult;
import com.central.common.model.SysTansterMoneyLog;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.db.mapper.SuperMapper;
import com.central.user.model.co.SysTansterMoneyPageCo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysTansterMoneyLogMapper extends SuperMapper<SysTansterMoneyLog> {

    List<SysTansterMoneyLog> findList(Page<SysTansterMoneyLog> page, @Param("u") SysTansterMoneyPageCo params);

    List<SysTansterMoneyLogVo> findAllByParent(@Param("u") SysTansterMoneyPageCo params);
}
