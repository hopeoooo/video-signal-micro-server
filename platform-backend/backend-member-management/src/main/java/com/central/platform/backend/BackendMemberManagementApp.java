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
 * 平台后端管理系统服-会员管理
 */
@EnableFeignInterceptor
@EnableFeignClients(basePackages = "com.central")
@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
@ComponentScans(value = {
        @ComponentScan(value = "com.central")
})
public class BackendMemberManagementApp {
    public static void main(String[] args) {
        SpringApplication.run(BackendMemberManagementApp.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
