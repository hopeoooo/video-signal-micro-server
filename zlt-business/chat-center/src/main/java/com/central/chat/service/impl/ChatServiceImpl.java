package com.central.chat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.central.chat.constants.ChatConstants;
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
    public void syncSaveChatMessage(String roomId, String message) {
        String key = ChatConstants.CHAT_MESSAGE_KEY + roomId;
        redisRepository.in(key, message);
        System.out.println("aaa" + Thread.currentThread().getId());
    }

    @Override
    public List<MessageVo> getChatMessageByRoomId(String roomId) {
        List<MessageVo> list = new ArrayList<>();
        Long length = redisRepository.length(ChatConstants.CHAT_MESSAGE_KEY + roomId);
        if (length == 0) {
            return list;
        }
        List<Object> redisList = redisRepository.getList(ChatConstants.CHAT_MESSAGE_KEY + roomId, 0, length.intValue());
        for (Object obj : redisList) {
            MessageVo vo = JSONObject.parseObject(obj.toString(), MessageVo.class);
            list.add(vo);
        }
        return list;
    }
}
