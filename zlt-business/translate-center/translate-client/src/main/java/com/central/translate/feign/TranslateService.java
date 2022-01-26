package com.central.translate.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.params.translate.QueryI18nInfoPageParam;
import com.central.common.params.translate.UpdateI18nInfoParam;
import com.central.common.vo.I18nInfoPageVO;
import com.central.translate.feign.callback.TranslateServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 翻译服务feign
 *
 * @author lance
 * @since 2022 -01-25 13:23:38
 */
@FeignClient(name = ServiceNameConstants.TRANSLATE_SERVICE, fallbackFactory = TranslateServiceFallbackFactory.class, decode404 = true)
public interface TranslateService {

    /**
     * 更新国际化字典
     *
     * @param param 入参释义
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-25 13:23:38
     */
    @PostMapping("/translate/update")
    Result<String> update(@RequestBody UpdateI18nInfoParam param);

    /**
     * 查询国际化字典分页
     *
     * @param param 入参释义
     * @return {@link PageResult} 出参释义
     * @author lance
     * @since 2022 -01-25 13:25:54
     */
    @GetMapping("/translate/infos")
    PageResult<I18nInfoPageVO> infos(@ModelAttribute QueryI18nInfoPageParam param);


    /**
     * 获取所有的国际化资源
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-25 14:34:28
     */
    @GetMapping("/translate/fullSource")
    Result<I18nSourceDTO> fullSource();

}