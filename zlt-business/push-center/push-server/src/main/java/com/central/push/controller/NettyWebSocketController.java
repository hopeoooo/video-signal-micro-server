package com.central.push.controller;

import com.central.common.model.Result;
import com.central.push.config.NettyWebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @GetMapping(value = "/sendAll/{message}")
    @ApiOperation(value = "群发消息")
    public Result sendAllMessage(@PathVariable(name = "message") String message){
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
    @GetMapping(value = "/sendOne/{userName}/{message}")
    public Result sendOneMessage(@PathVariable(name = "userName") String userName, @PathVariable(name = "message") String message){
        String msg = NettyWebSocketServer.sendOneMessage(message, userName);
        if(StringUtils.isBlank(msg)){
            return Result.succeed(userName + "消息推送成功");
        }
        return Result.failed(msg);
    }
}