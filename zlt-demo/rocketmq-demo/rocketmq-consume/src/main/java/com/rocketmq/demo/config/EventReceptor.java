package com.rocketmq.demo.config;

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
public class EventReceptor {

    @Bean
    public Function<Flux<Message<String>>, Mono<Void>> gaaraSimple() {
        return flux -> flux.map(message -> {
            // 收到发送的消息
            System.out.println("消费者，gaaraSimple: "+message.getPayload());
            return message;
        }).then();
    }

    // 第二种方式
    // 注意使用Flux 要调用 subscribe 不然这个方法不会被消费
//    @Bean
    public Consumer<Flux<Message<String>>> consumerEvent_1() {
        return flux -> flux.map(message -> {
            // 收到发送的消息
            System.out.println("consumerEvent_1: "+message.getPayload());
            return message;
        }).subscribe();
    }
    // 或
//    @Bean
    public Consumer<Message<String>> consumerEvent_2() {
        // 收到发送的消息
        return message -> System.out.println("consumerEvent_2: "+message.getPayload());
    }



}