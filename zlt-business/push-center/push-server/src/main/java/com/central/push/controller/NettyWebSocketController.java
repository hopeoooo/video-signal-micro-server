package com.central.push.controller;

import com.central.common.model.Result;
import com.central.push.config.NettyWebSocketRoomServer;
import com.central.push.config.NettyWebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ws/api")
@Api(tags = "webSocket消息推送")
public class NettyWebSocketController {

    /**
     * 群发消息内容
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/sendAll")
    @ApiOperation(value = "群发消息")
    public Result sendAllMessage(@RequestParam("message") String message) {
        NettyWebSocketServer.batchSendMessage(message);
        return Result.succeed("群消息发送成功");
    }

    /**
     * 指定用户发消息
     *
     * @param message  消息内容
     * @param userName 用户名
     * @return
     */
    @ApiOperation(value = "指定用户推发消息")
    @PostMapping(value = "/sendOne")
    public Result sendOneMessage(@RequestParam("userName") String userName, @RequestParam("message") String message) {
        String msg = NettyWebSocketServer.sendOneMessage(message, userName);
        if (StringUtils.isBlank(msg)) {
            return Result.succeed(userName + "消息推送成功");
        }
        return Result.failed(msg);
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
        String msg = NettyWebSocketRoomServer.sendMessageByRoomId(roomId, message);
        if (StringUtils.isBlank(msg)) {
            return Result.succeed(roomId + "号房间消息推送成功");
        }
        return Result.failed(msg);
    }
}