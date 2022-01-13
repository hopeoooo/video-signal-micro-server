package com.central.push.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import com.central.push.feign.callback.PushServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

;

/**
 * @author zlt
 */
@FeignClient(name = ServiceNameConstants.PUSH_SERVICE, fallbackFactory = PushServiceFallbackFactory.class, decode404 = true)
public interface PushService {

    /**
     * 创建连接
     * @param userId
     * @return
     */
    @GetMapping("/sse/connect/{userId}")
    SseEmitter connect(@PathVariable("userId") String userId);

    /**
     * 群发消息
     * @param message
     * @return
     */
    @GetMapping("/sse/push/{message}")
    Result<String> push(@PathVariable(name = "message") String message);

    /**
     * 私发消息
     * @param message
     * @param userId
     * @return
     */
    @GetMapping("/sse/pushOne/{messsage}/{userId}")
    Result<String> pushOne(@PathVariable(name = "message") String message, @PathVariable(name = "userId") String userId);

    /**
     * 关闭连接
     * @param userId
     * @return
     */
    @GetMapping("/sse/close/{userId}")
    Result<String> close(@PathVariable("userId") String userId);

}