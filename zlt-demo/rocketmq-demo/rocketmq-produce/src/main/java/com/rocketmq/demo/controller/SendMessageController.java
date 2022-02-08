package com.rocketmq.demo.controller;

import com.rocketmq.demo.model.Order;
import com.rocketmq.demo.service.impl.MessageProviderImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/rmq")
public class SendMessageController {

    @Resource
    private MessageProviderImpl messageProvider;

    @Autowired
    private StreamBridge streamBridge;

    @GetMapping("/send")
    public void send(){
        boolean sendResult = streamBridge.send("gaaraSimple-out-0", "Body Content Data");
        log.info("[send][发送消息完成, 结果 = {}]", sendResult);
    }

    @GetMapping("/send_order")
    public void sendOrder(){
        String uuid = UUID.randomUUID().toString();
        Order order = new Order(uuid);
        // 发送消息
        boolean sendResult = streamBridge.send("gaaraOrder-out-0", order);
        log.info("[sendOrder][发送消息完成, 结果 = {}]", sendResult);
    }

    @GetMapping("/send_delay")
    public void sendDelay(){
        String uuid = UUID.randomUUID().toString();
        Order order = new Order(uuid);
        // 创建 Spring Message 对象
        Message springMessage = MessageBuilder.withPayload(order)
                .setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, "3") // 设置延迟级别为 3，10 秒后消费。
                .build();
        // 发送消息
        boolean sendResult = streamBridge.send("gaaraOrder-out-0", springMessage);
        log.info("[sendDelay][发送消息完成, 结果 = {}]", sendResult);
    }

    @GetMapping("/send_tag")
    public void sendTag(){
        for (String tag : new String[]{"Pay-Order", "Topup-Order", "Buy-Order"}) {
            String uuid = UUID.randomUUID().toString();
            Order order = new Order(uuid);
            // 创建 Spring Message 对象
            Message<Order> springMessage = MessageBuilder.withPayload(order)
                    .setHeader(MessageConst.PROPERTY_TAGS, tag) // 设置 Tag
                    .build();
            // 发送消息
            boolean sendResult = streamBridge.send("gaaraOrder-out-0", springMessage);
            log.info("[sendTag][发送消息完成, 结果 = {}]", sendResult);
        }
    }

}
