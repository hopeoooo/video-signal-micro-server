package com.central.chat.service;

import com.central.chat.vo.MessageVo;

import java.util.List;

public interface ChatService {
    
    void syncSaveChatMessage(String groupId,String message);

    List<MessageVo> getChatMessageByGroupId(String groupId);
}
