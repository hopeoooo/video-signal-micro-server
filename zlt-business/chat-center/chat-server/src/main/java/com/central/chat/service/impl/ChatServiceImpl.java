package com.central.chat.service.impl;

import com.alibaba.fastjson.JSON;
import com.central.chat.constant.ChatConstant;
import com.central.chat.service.ChatService;
import com.central.chat.vo.MessageVo;
import com.central.common.redis.template.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private RedisRepository redisRepository;

    @Override
    @Async
    public void syncSaveChatMessage(String groupId, String message) {
        String key = ChatConstant.CHAT_MESSAGE_KEY + groupId;
        redisRepository.in(key, message);
        System.out.println("aaa" + Thread.currentThread().getId());
    }

    @Override
    public List<MessageVo> getChatMessageByGroupId(String groupId) {
        List<MessageVo> list = new ArrayList<>();
        Long length = redisRepository.length(ChatConstant.CHAT_MESSAGE_KEY + groupId);
        if (length == 0) {
            return list;
        }
        List<Object> redisList = redisRepository.getList(ChatConstant.CHAT_MESSAGE_KEY + groupId, 0, length.intValue());
        list = JSON.parseArray(redisList.toString(), MessageVo.class);
        return list;
    }
}
