package com.rocketmq.demo.config;

import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 注册消费者bean
 */
@Configuration
public class ConsumerOrderBeans {

    @Bean
    public Function<Flux<Message<String>>, Mono<Void>> gaaraOrder() {
        return flux -> flux.map(message -> {
            // 收到发送的消息
            System.out.println("消费者，gaaraOrder: "+message.getPayload());
            System.out.println("TAG: "+message.getHeaders().get("rocketmq_"+MessageConst.PROPERTY_TAGS));
            return message;
        }).then();
    }

    @Bean
    public Function<Flux<Message<String>>, Mono<Void>> gaaraOrderA() {
        return flux -> flux.map(message -> {
            // 收到发送的消息
            System.out.println("消费者，gaaraOrderA: "+message.getPayload());
            System.out.println("TAG: "+message.getHeaders().get("rocketmq_"+MessageConst.PROPERTY_TAGS));
            return message;
        }).then();
    }


}