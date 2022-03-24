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
@RequestMapping("/chat")
@Api(tags = "聊天信息")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping(value = "/getChatMessageByGroupId/{groupId}")
    @ApiOperation(value = "根据房间ID查询聊天信息")
    public Result<List<MessageVo>> getChatMessageByGroupId(@PathVariable String groupId) {
        List<MessageVo> list = chatService.getChatMessageByGroupId(groupId);
        return Result.succeed(list);
    }


    /**
     * 指定房间发消息
     *
     * @param message 消息内容
     * @param groupId  群组ID
     * @return
     */
    @ApiOperation(value = "指定房间推发消息")
    @PostMapping(value = "/sendMessageByGroupId")
    public Result sendMessageByGroupId(@RequestParam("groupId") String groupId, @RequestParam("message") String message) {
        String msg = NettyWebSocketServer.sendMessageByGroupId(groupId, message);
        if (StringUtils.isBlank(msg)) {
            return Result.succeed(groupId + "号群组消息推送成功");
        }
        return Result.failed(msg);
    }
}