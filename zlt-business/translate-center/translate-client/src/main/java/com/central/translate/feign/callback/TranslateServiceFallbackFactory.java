package com.central.translate.feign.callback;

import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.translate.model.co.QueryI18nInfoPageCo;
import com.central.translate.model.co.UpdateI18nInfoCo;
import com.central.common.vo.I18nInfoPageVO;
import com.central.common.vo.LanguageLabelVO;
import com.central.translate.feign.TranslateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 降级工场
 */
@Slf4j
@Component
public class TranslateServiceFallbackFactory implements FallbackFactory<TranslateService> {

    @Override
    public TranslateService create(Throwable cause) {
        return new TranslateService() {
            @Override
            public Result<String> backendUpdate(UpdateI18nInfoCo param) {
                log.error("调用失败: {}", param);
                return null;
            }

            @Override
            public Result<String> frontUpdate(UpdateI18nInfoCo param) {
                log.error("调用失败: {}", param);
                return null;
            }

            @Override
            public Result<PageResult<I18nInfoPageVO>> infos(QueryI18nInfoPageCo param) {
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
