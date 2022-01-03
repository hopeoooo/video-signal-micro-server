package com.central.user.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.dto.LoginLogPageDto;
import com.central.common.model.LoginLog;
import com.central.common.model.PageResult;
import com.central.common.model.SuperPage;
import com.central.db.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface LoginLogMapper extends SuperMapper<LoginLog> {
    /**
     * 查询日志
     * @return
     */
    List<LoginLogPageDto> findAllLoginLog(Page<LoginLogPageDto> page, @Param("p") Map<String, Object> params);
}
