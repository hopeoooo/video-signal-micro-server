package com.central.game.rocketMq.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * 注册消费者bean
 */
//@Configuration
public class WashCodeConsumer {

//    @Bean
    public Function<Flux<Message<String>>, Mono<Void>> washCode() {
        return flux -> flux.map(message -> {
            // 收到发送的消息
            System.out.println("消费者，washCode: "+message.getPayload());
            return message;
        }).then();
    }
}