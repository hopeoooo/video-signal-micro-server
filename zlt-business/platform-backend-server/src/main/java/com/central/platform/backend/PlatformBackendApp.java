package com.central.platform.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * app后端管理系统服
 */
@EnableFeignClients(basePackages = "com.central.common")
@EnableDiscoveryClient
@SpringBootApplication
public class PlatformBackendApp {
    public static void main(String[] args) {
        SpringApplication.run(PlatformBackendApp.class, args);
    }
}
