package com.central.file.annotation;

import com.central.file.feign.callback.FileServiceFallbackFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用配置服Feign接口
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableFeignClients(basePackages = "com.central")
@Import({FileServiceFallbackFactory.class})
public @interface EnableFileClient {

}
