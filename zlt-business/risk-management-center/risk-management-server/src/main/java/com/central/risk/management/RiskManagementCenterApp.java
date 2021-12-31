package com.central.risk.management;

import com.central.common.lb.annotation.EnableFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 风控服
 */
@EnableDiscoveryClient
@EnableFeignInterceptor
@EnableFeignClients(basePackages = "com.central.risk.management")
@SpringBootApplication
public class RiskManagementCenterApp {
    public static void main(String[] args) {
        SpringApplication.run(RiskManagementCenterApp.class, args);
    }
}
