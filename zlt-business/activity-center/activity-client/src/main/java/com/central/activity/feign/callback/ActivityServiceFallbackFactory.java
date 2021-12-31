package com.central.activity.feign.callback;

import com.central.activity.feign.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * activityService降级工场
 */
@Slf4j
public class ActivityServiceFallbackFactory implements FallbackFactory<ActivityService> {

    @Override
    public ActivityService create(Throwable cause) {
        return null;
    }
}
