package com.central.game.rabbitMq;

import com.alibaba.fastjson.JSONObject;
import com.central.common.model.PushResult;
import com.central.common.model.Result;
import com.central.game.constants.GameListEnum;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.service.IGameRoomInfoOfflineService;
import org.springframework.amqp.core.Message;
import com.rabbitmq.client.Channel;
import com.central.push.feign.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.plugin2.message.EventMessage;

import java.util.Date;

/**
 * 百家乐桌台信息详情
 */
@Component
@RabbitListener(queues = "error_queue")
@Slf4j
public class ErrorMessageConsumer {

    @Autowired
    private IGameRoomInfoOfflineService gameRoomInfoOfflineService;
    @Autowired
    private PushService pushService;

    @RabbitHandler
    public void process(String data, Message message, Channel channel) {
        log.info("接收到桌台配置详情数据,data={}", data);
        MessageProperties messageProperties = message.getMessageProperties();
        Object header = messageProperties.getHeader("x-exception-message");
        Object header1 = messageProperties.getHeader("x-original-routingKey");
        Object header2 = messageProperties.getHeader("x-original-exchange");
        Object header3 = messageProperties.getHeader("x-exception-stacktrace");
    }

}
