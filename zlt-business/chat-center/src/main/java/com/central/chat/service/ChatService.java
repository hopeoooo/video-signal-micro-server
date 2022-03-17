package com.central.chat.service;

import com.central.chat.vo.MessageVo;

import java.util.List;

public interface ChatService {
    
    void syncSaveChatMessage(String roomId,String message);

    List<MessageVo> getChatMessageByRoomId(String roomId);
}
