package com.central.activity;

import com.central.activity.annotation.EnableActivityClient;
import com.central.common.lb.annotation.EnableFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * 活动服
 */
@EnableDiscoveryClient
@EnableFeignInterceptor
@EnableFeignClients(basePackages = "com.central")
@SpringBootApplication
@ComponentScans(value = {
        @ComponentScan(value = "com.central")
})
public class ActivityCenterApp {
    public static void main(String[] args) {
        SpringApplication.run(ActivityCenterApp.class, args);
    }
}
