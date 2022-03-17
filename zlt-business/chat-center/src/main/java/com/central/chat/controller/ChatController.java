package com.central.chat.controller;

import com.central.chat.service.ChatService;
import com.central.chat.vo.MessageVo;
import com.central.common.model.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
@Api(tags = "聊天信息")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping(value = "/getChatMessageByRoomId/{roomId}")
    @ApiOperation(value = "根据房间ID查询聊天信息")
    public Result<List<MessageVo>> getChatMessageByRoomId(@PathVariable String roomId) {
        List<MessageVo> list = chatService.getChatMessageByRoomId(roomId);
        return Result.succeed(list);
    }
}