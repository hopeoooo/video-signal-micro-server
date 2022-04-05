package com.central.game.rabbitMq;

import com.central.game.model.MqConsumerErrorMsg;
import com.central.game.service.IMqConsumerErrorMsgService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 百家乐桌台信息详情
 */
@Component
@RabbitListener(queues = "error_queue")
@Slf4j
public class ErrorMessageConsumer {

    @Autowired
    private IMqConsumerErrorMsgService mqConsumerErrorMsgService;

    @RabbitHandler
    public void process(String data, Message message, Channel channel) {
        log.info("错误MQ信息,data={}", data);
        MessageProperties messageProperties = message.getMessageProperties();
        Object exMessage = messageProperties.getHeader("x-exception-message");
        Object routingKey = messageProperties.getHeader("x-original-routingKey");
        Object exchange = messageProperties.getHeader("x-original-exchange");
        Object stacktrace = messageProperties.getHeader("x-exception-stacktrace");
        MqConsumerErrorMsg msg = new MqConsumerErrorMsg();
        msg.setMessage(data);
        if (ObjectUtils.isNotEmpty(exMessage)) {
            msg.setExMessage(exMessage.toString());
        }
        if (ObjectUtils.isNotEmpty(routingKey)) {
            msg.setRoutingKey(routingKey.toString());
        }
        if (ObjectUtils.isNotEmpty(exchange)) {
            msg.setExchange(exchange.toString());
        }
        if (ObjectUtils.isNotEmpty(stacktrace)) {
            msg.setExStackTrace(stacktrace.toString());
        }
        mqConsumerErrorMsgService.save(msg);
    }
}
