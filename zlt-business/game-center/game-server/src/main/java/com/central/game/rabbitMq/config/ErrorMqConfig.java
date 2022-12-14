package com.central.game.rabbitMq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorMqConfig {

    @Bean("error_direct")
    public DirectExchange errorMessageExchange(){
        return new DirectExchange("error_direct");
    }
    @Bean("error_queue")
    public Queue errorQueue(){
        return new Queue("error_queue",true);
    }
    @Bean
    public Binding bindingerror(DirectExchange error_direct, Queue error_queue){
        return  BindingBuilder.bind(error_queue).to(error_direct).with("error");
    }
    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate,"error_direct","error");
    }

}
