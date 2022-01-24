package com.central.translate.annotation;

import com.central.translate.feign.callback.TranslateServiceFallbackFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 控制是否加载搜索中心客户端的Service
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({TranslateServiceFallbackFactory.class})
@EnableFeignClients(basePackages = "com.central")
public @interface EnableTranslateClient {

}
