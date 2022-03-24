package com.central.chat.feign.callback;

import com.central.chat.feign.ChatService;
import com.central.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * PushService降级工场
 */
@Slf4j
public class ChatServiceFallbackFactory implements FallbackFactory<ChatService> {

    @Override
    public ChatService create(Throwable throwable) {
        return new ChatService() {
            @Override
            public Result sendMessageByGroupId(String groupId, String message) {
                log.error("sendMessageByGroupId给群组:{}推送消息异常",groupId);
                return Result.failed("推送群组消息失败");
            }
        };
    }
}
