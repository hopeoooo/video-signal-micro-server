package com.central.translate.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import com.central.translate.model.co.I18nPositionCo;
import com.central.common.vo.I18nPositionVO;
import com.central.translate.feign.callback.TranslatePositionFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 注释
 *
 * @author lance
 * @since 2022 -01-25 13:13:35
 */
@FeignClient(name = ServiceNameConstants.TRANSLATE_SERVICE, fallbackFactory = TranslatePositionFallbackFactory.class, decode404 = true)
public interface TranslatePositionService {


    /**
     * 翻译页面位置列表
     *
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-25 13:13:35
     */
    @GetMapping("/translate/position/pageList")
    List<I18nPositionVO> pageList();

    /**
     * 页面下的所有位置
     *
     * @param pid 入参释义
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-25 13:13:35
     */
    @GetMapping("/translate/position/byPid")
    List<I18nPositionVO> positions(@RequestParam("pid") Long pid);

    /**
     * 新增位置
     *
     * @param position 入参释义
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-25 13:32:11
     */
    @PostMapping("/translate/position/saveOrUpdate")
    Result<String> saveOrUpdate(@RequestBody I18nPositionCo position);

}
