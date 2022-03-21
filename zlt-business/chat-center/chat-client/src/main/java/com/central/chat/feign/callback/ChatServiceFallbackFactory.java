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
            public Result sendMessageByRoomId(String roomId, String message) {
                log.error("sendMessageByRoomId给房间:{}推送消息异常",roomId);
                return Result.failed("推送房间消息失败");
            }
        };
    }
}
