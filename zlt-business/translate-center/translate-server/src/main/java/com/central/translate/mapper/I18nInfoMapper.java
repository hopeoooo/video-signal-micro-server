package com.central.translate.mapper;

import com.central.common.model.I18nInfo;
import com.central.db.mapper.SuperMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.translate.model.co.I18nInfoPageMapperCo;
import com.central.common.vo.I18nInfoPageVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 注释
 *
 * @author lance
 * @date 2022 -01-24 21:19:58
 * @since 2022 -01-25 12:28:42
 */
@Mapper
public interface I18nInfoMapper extends SuperMapper<I18nInfo> {
    /**
     * 分页查询列表
     *
     * @param page   入参释义
     * @param params 入参释义
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-25 12:28:42
     */
    List<I18nInfo> findList(Page<I18nInfo> page, @Param("p") Map<String, Object> params);


    /**
     * 分页查询列表
     *
     * @param page   入参释义
     * @param params 入参释义
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-25 12:28:42
     */
    List<I18nInfoPageVO> findPage(Page<I18nInfoPageVO> page, @Param("p") I18nInfoPageMapperCo params);

}
