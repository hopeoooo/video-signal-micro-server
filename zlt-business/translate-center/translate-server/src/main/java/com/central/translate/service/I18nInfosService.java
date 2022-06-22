package com.central.translate.service;

import com.central.common.model.PageResult;
import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.I18nInfo;
import com.central.common.service.ISuperService;
import com.central.translate.model.co.QueryI18nInfoPageCo;
import com.central.translate.model.co.SaveI18nInfoCo;
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

    List<I18nInfo> findListByZhCn(Integer fromOf,String zhCn);
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
    I18nSourceDTO getFrontFullI18nSource(Integer fromOf);

    /**
     * 初始化国际化资源redis缓存
     *
     * @author lance
     * @since 2022 -01-25 17:24:33
     */
    void initI18nSourceRedis();

    /**
     * 更新国际化字典
     *
     * @param from  0前台 1后台
     * @param param 入参释义
     * @return {@link boolean} 出参释义
     * @author lance
     * @since 2022 -02-17 20:30:46
     */
    boolean updateI18nInfo(Integer from,UpdateI18nInfoCo param);


    /**
     * 新增国际化字典
     *
     * @param from  入参释义
     * @param param 入参释义
     * @return {@link boolean} 出参释义
     * @author lance
     * @since 2022 -02-17 20:36:55
     */
    boolean saveI18nInfo(Integer from, SaveI18nInfoCo param);


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
