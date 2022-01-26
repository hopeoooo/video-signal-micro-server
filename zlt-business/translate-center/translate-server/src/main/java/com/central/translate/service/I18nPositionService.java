package com.central.translate.service;

import com.central.common.vo.I18nPositionVO;
import com.central.common.model.I18nPosition;
import com.central.common.service.ISuperService;

import java.util.List;

/**
 * 国际化定位表
 *
 * @author lance
 * @since 2022 -01-25 11:39:46
 */
public interface I18nPositionService extends ISuperService<I18nPosition> {

    /**
     * 查询所有的页面
     *
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-25 13:04:41
     */
    List<I18nPositionVO> findAllPage();


    /**
     * 查询子级
     *
     * @param pid 入参释义
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-25 13:05:23
     */
    List<I18nPositionVO> findByPid(Long pid);

}
