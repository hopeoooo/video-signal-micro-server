package com.central.push.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import com.central.push.feign.callback.PushServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

;

/**
 * @author zlt
 */
@FeignClient(name = ServiceNameConstants.PUSH_SERVICE, fallbackFactory = PushServiceFallbackFactory.class, decode404 = true)
public interface PushService {

    /**
     * 群发消息
     * @param message
     * @return
     */
    @GetMapping(value = "/ws/api/sendAll/{message}")
    public Result sendAllMessage(@PathVariable(name = "message") String message);

    /**
     * 私发消息
     * @param message
     * @param userName
     * @return
     */
    @GetMapping(value = "/ws/api/sendOne/{userName}/{message}")
    public Result sendOneMessage(@PathVariable(name = "userName") String userName, @PathVariable(name = "message") String message);


}