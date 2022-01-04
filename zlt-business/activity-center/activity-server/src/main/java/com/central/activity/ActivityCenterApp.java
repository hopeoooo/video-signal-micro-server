package com.central.activity;

import com.central.activity.annotation.EnableActivityClient;
import com.central.common.lb.annotation.EnableFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 活动服
 */
@EnableDiscoveryClient
@EnableFeignInterceptor
@EnableFeignClients(basePackages = "com.central")
@SpringBootApplication
public class ActivityCenterApp {
    public static void main(String[] args) {
        SpringApplication.run(ActivityCenterApp.class, args);
    }
}
