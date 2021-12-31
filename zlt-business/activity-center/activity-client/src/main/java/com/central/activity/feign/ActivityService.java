package com.central.activity.feign;

import com.central.activity.feign.callback.ActivityServiceFallbackFactory;
import com.central.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

;

/**
 */
@FeignClient(name = ServiceNameConstants.ACTIVITY_SERVICE, fallbackFactory = ActivityServiceFallbackFactory.class, decode404 = true)
public interface ActivityService {
    /**
     * 查询游戏列表
     */
    @PostMapping(value = "/activity/list")
    String list();
}