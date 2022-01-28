package com.central.translate.feign.callback;

import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.params.translate.QueryI18nInfoPageParam;
import com.central.common.params.translate.UpdateI18nInfoParam;
import com.central.common.vo.I18nInfoPageVO;
import com.central.common.vo.LanguageLabelVO;
import com.central.translate.feign.TranslateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

/**
 * 降级工场
 */
@Slf4j
public class TranslateServiceFallbackFactory implements FallbackFactory<TranslateService> {

    @Override
    public TranslateService create(Throwable cause) {
        return new TranslateService() {
            @Override
            public Result<String> backendUpdate(UpdateI18nInfoParam param) {
                log.error("调用失败: {}", param);
                return null;
            }

            @Override
            public Result<String> frontUpdate(UpdateI18nInfoParam param) {
                log.error("调用失败: {}", param);
                return null;
            }

            @Override
            public PageResult<I18nInfoPageVO> infos(QueryI18nInfoPageParam param) {
                log.error("调用失败: {}", param);
                return null;
            }

            @Override
            public Result<I18nSourceDTO> backendFullSource() {
                log.error("调用失败");
                return null;
            }

            @Override
            public Result<I18nSourceDTO> frontFullSource() {
                log.error("调用失败");
                return null;
            }

            @Override
            public List<LanguageLabelVO> languageLabel() {
                log.error("调用失败");
                return null;
            }
        };
    }
}
