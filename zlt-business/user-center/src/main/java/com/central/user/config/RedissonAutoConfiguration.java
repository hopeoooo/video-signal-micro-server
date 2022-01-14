package com.central.user.config;

import com.central.user.util.RedissLockUtil;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonAutoConfiguration {


    @Bean
    RedissLockUtil redissLockUtil(RedissonClient redissonClient){
        RedissLockUtil redissLockUtil = new RedissLockUtil();
        redissLockUtil.setRedissonClient(redissonClient);
        return redissLockUtil;
    }
}
