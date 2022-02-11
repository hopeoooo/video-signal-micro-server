package com.central.translate.service;

import com.central.common.model.PageResult;
import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.I18nInfo;
import com.central.common.service.ISuperService;
import com.central.translate.model.co.QueryI18nInfoPageCo;
import com.central.translate.model.co.UpdateI18nInfoCo;
import com.central.common.vo.I18nInfoPageVO;
import com.central.common.vo.LanguageLabelVO;

import java.util.List;


/**
 * 国际化字典表
 *
 * @author lance
 * @since 2022 -01-25 11:38:59
 */
public interface I18nInfosService extends ISuperService<I18nInfo> {

    /**
     * 获取所有的后台国际化资源
     *
     * @return {@link I18nSourceDTO} 国际化资源
     * @author lance
     * @since 2022 -01-25 11:58:22
     */
    I18nSourceDTO getBackendFullI18nSource();


    /**
     * 获取所有的前台国际化资源
     *
     * @return {@link I18nSourceDTO} 国际化资源
     * @author lance
     * @since 2022 -01-28 12:44:38
     */
    I18nSourceDTO getFrontFullI18nSource();

    /**
     * 初始化国际化资源redis缓存
     *
     * @author lance
     * @since 2022 -01-25 17:24:33
     */
    void initI18nSourceRedis();


    /**
     * 更新后台国际化字典
     *
     * @param param    更新参数
     * @return {@link boolean} 是否成功
     * @author lance
     * @since 2022 -01-25 12:14:35
     */
    boolean updateBackendI18nInfo(UpdateI18nInfoCo param);


    /**
     * 更新前台国际化字典
     *
     * @param param    更新参数
     * @return {@link boolean} 是否成功
     * @author lance
     * @since 2022 -01-28 12:21:47
     */
    boolean updateFrontI18nInfo(UpdateI18nInfoCo param);


    /**
     * 查询国际化字典分页
     *
     * @param param 查询参数
     * @return {@link PageResult} 分页数据
     * @author lance
     * @since 2022 -01-25 12:25:12
     */
    PageResult<I18nInfoPageVO> findInfos(QueryI18nInfoPageCo param);


    /**
     * 获取语言标签
     *
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-28 13:34:54
     */
    List<LanguageLabelVO> getLanguageLabel();

}
