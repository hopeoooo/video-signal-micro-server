package com.central.platform.backend;

import com.central.common.lb.annotation.EnableFeignInterceptor;
import com.central.config.annotation.EnableConfigClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * app后端管理系统服
 */
@EnableConfigClient
@EnableFeignInterceptor
@EnableFeignClients(basePackages = "com.central")
@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
public class PlatformBackendApp {
    public static void main(String[] args) {
        SpringApplication.run(PlatformBackendApp.class, args);
    }
}
