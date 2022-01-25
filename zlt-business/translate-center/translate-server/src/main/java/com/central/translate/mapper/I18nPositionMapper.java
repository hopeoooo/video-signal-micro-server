package com.central.translate.mapper;

import com.central.common.model.I18nPosition;
import com.central.db.mapper.SuperMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author lance
 * @date 2022-01-24 21:19:58
 */
@Mapper
public interface I18nPositionMapper extends SuperMapper<I18nPosition> {
    /**
     * 分页查询用户列表
     * @param page
     * @param params
     * @return
     */
    List<I18nPosition> findList(Page<I18nPosition> page, @Param("p") Map<String, Object> params);
}
