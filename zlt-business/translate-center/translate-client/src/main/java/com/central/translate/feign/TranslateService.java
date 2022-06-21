package com.central.translate.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.translate.model.co.QueryI18nInfoPageCo;
import com.central.translate.model.co.SaveI18nInfoCo;
import com.central.translate.model.co.UpdateI18nInfoCo;
import com.central.common.vo.I18nInfoPageVO;
import com.central.common.vo.LanguageLabelVO;
import com.central.translate.feign.callback.TranslateServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 翻译服务feign
 *
 * @author lance
 * @since 2022 -01-25 13:23:38
 */
@FeignClient(name = ServiceNameConstants.TRANSLATE_SERVICE, fallbackFactory = TranslateServiceFallbackFactory.class, decode404 = true)
public interface TranslateService {

    /**
     * 更新后台国际化字典
     *
     * @param param 入参释义
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-25 13:23:38
     */
    @PostMapping("/translate/backendUpdate")
    Result<String> backendUpdate(@RequestBody UpdateI18nInfoCo param);


    /**
     * 更新前台国际化字典
     *
     * @param param 入参释义
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-28 18:13:04
     */
    @PostMapping("/translate/frontUpdate")
    Result<String> frontUpdate(@RequestBody UpdateI18nInfoCo param);

    /**
     * 新增后台国际化字典
     *
     * @param param   更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-25 14:13:51
     */
    @PostMapping("/translate/backendSave")
    Result<String> backendSave(@RequestBody SaveI18nInfoCo param);

    /**
     * 新增前台国际化字典
     *
     * @param param   更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-28 12:46:24
     */
    @PostMapping("/translate/frontSave")
    Result<String> frontSave(@RequestBody SaveI18nInfoCo param);

    /**
     * 查询国际化字典分页
     *
     * @param param 入参释义
     * @return {@link PageResult} 出参释义
     * @author lance
     * @since 2022 -01-25 13:25:54
     */
    @GetMapping("/translate/infos")
    Result<PageResult<I18nInfoPageVO>> infos(@SpringQueryMap QueryI18nInfoPageCo param);


    /**
     * 获取所有的后台国际化资源
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-25 14:34:28
     */
    @GetMapping("/translate/backendFullSource")
    Result<I18nSourceDTO> backendFullSource();

    /**
     * 获取所有的前台PC国际化资源
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-28 13:17:46
     */
    @GetMapping("/translate/frontFullSource")
    Result<I18nSourceDTO> frontFullSource();

    /**
     * 获取所有的前台APP国际化资源
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-28 13:17:46
     */
    @GetMapping("/translate/frontAppFullSource")
    Result<I18nSourceDTO> frontAppFullSource();

    /**
     * 获取所有的前台message国际化资源
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-28 13:17:46
     */
    @GetMapping("/translate/frontMessageFullSource")
    Result<I18nSourceDTO> frontMessageFullSource();

    /**
     * 获取语言标签
     *
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-28 13:44:41
     */
    @GetMapping("/translate/languageLabel")
    List<LanguageLabelVO> languageLabel();

}