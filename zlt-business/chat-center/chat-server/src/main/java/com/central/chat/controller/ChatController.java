package com.central.chat.controller;

import com.central.chat.config.NettyWebSocketServer;
import com.central.chat.service.ChatService;
import com.central.chat.vo.MessageVo;
import com.central.common.model.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/com/central/chat")
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


    /**
     * 指定房间发消息
     *
     * @param message 消息内容
     * @param roomId  房间ID
     * @return
     */
    @ApiOperation(value = "指定房间推发消息")
    @PostMapping(value = "/sendMessageByRoomId")
    public Result sendMessageByRoomId(@RequestParam("roomId") String roomId, @RequestParam("message") String message) {
        String msg = NettyWebSocketServer.sendMessageByRoomId(roomId, message);
        if (StringUtils.isBlank(msg)) {
            return Result.succeed(roomId + "号房间消息推送成功");
        }
        return Result.failed(msg);
    }
}