package com.central.agent;

import com.central.common.lb.annotation.EnableFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 代理服
 */
@EnableDiscoveryClient
@EnableFeignInterceptor
@EnableFeignClients( basePackages = "com.central") // 针对UserService的feign接口，需要pom添加zlt-loadbalancer-spring-boot-starter
@SpringBootApplication
public class AgentCenterApp {
    public static void main(String[] args) {
        SpringApplication.run(AgentCenterApp.class, args);
    }
}
