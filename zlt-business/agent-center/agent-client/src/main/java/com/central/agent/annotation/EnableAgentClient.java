package com.central.agent.annotation;

import com.central.agent.feign.callback.AgentServiceFallbackFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 控制是否加载搜索中心客户端的Service
 *
 * @author zlt
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)

@Import({AgentServiceFallbackFactory.class}) // , QueryS@EnableFeignClients(basePackages = "com.central")
@EnableFeignClients(basePackages = "com.central")  // service层扫描了，这里可以不写
//@EnableFeignClients(basePackages = "com.central.*.feign") // 不能写成这样
public @interface EnableAgentClient {

}
