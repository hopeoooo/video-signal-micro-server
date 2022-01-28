package com.central.translate.feign.callback;

import com.central.common.model.I18nPosition;
import com.central.common.model.Result;
import com.central.common.vo.I18nPositionVO;
import com.central.translate.feign.TranslatePositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

/**
 * 降级工场
 */
@Slf4j
public class TranslatePositionFallbackFactory implements FallbackFactory<TranslatePositionService> {

    @Override
    public TranslatePositionService create(Throwable cause) {
        return new TranslatePositionService(){
            @Override
            public List<I18nPositionVO> pageList() {
                log.error("调用失败");
                return null;
            }

            @Override
            public List<I18nPositionVO> positions(Long pid) {
                log.error("调用失败: {}", pid);
                return null;
            }

            @Override
            public Result<String> saveOrUpdate(I18nPosition position) {
                log.error("调用失败: {}", position);
                return null;
            }
        };
    }
}
