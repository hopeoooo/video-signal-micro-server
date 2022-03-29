package com.central.chat.feign;

import com.central.chat.feign.callback.ChatServiceFallbackFactory;
import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author zlt
 */
@FeignClient(name = ServiceNameConstants.CHAT_SERVICE, fallbackFactory = ChatServiceFallbackFactory.class, decode404 = true)
public interface ChatService {

    /**
     * 私发消息
     *
     * @param message
     * @param groupId
     * @return
     */
    @PostMapping(value = "/sendMessageByGroupId")
    Result sendMessageByGroupId(@RequestParam("groupId") String groupId, @RequestParam("message") String message);


}