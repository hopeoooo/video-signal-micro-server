package com.central.push.feign.callback;

import com.central.common.model.Result;
import com.central.push.feign.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * orderService降级工场
 */
@Slf4j
public class PushServiceFallbackFactory implements FallbackFactory<PushService> {

    @Override
    public PushService create(Throwable throwable) {
        return new PushService() {
            @Override
            public SseEmitter connect(String userId) {
                log.error("连接sse服务失败，userId:{}", userId, throwable);
                return null;
            }

            @Override
            public Result<String> push(String message) {
                log.error("群发消息失败，message:{}", message, throwable);
                return Result.failed("群发消息失败");
            }

            @Override
            public Result<String> pushOne(String message, String userId) {
                log.error("私发消息失败，userId:{}", userId, throwable);
                return Result.failed("私发消息失败");
            }

            @Override
            public Result<String> close(String userId) {
                log.error("sse服务关闭失败，userId:{}", userId, throwable);
                return Result.failed("sse服务关闭失败");
            }
        };
    }
}
