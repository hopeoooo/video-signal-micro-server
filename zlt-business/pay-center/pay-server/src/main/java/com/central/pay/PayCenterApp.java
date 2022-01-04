package com.central.pay;

import com.central.common.lb.annotation.EnableFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 支付服
 */
@EnableDiscoveryClient
@EnableFeignInterceptor
@EnableFeignClients(basePackages = "com.central")
@SpringBootApplication
public class PayCenterApp {
    public static void main(String[] args) {
        SpringApplication.run(PayCenterApp.class, args);
    }
}
