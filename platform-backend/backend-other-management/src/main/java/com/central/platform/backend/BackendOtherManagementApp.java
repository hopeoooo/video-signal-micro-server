package com.central.platform.backend;

import com.central.common.lb.annotation.EnableFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 平台后端管理系统服-综合管理
 * 不确定的业务边界，规划到综合管理中
 */
@EnableFeignInterceptor
@EnableFeignClients(basePackages = "com.central")
@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
@ComponentScans(value = {
        @ComponentScan(value = "com.central")
})
public class BackendOtherManagementApp {

    public static void main(String[] args) {
        SpringApplication.run(BackendOtherManagementApp.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
