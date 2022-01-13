package com.central.push.controller;

import com.central.common.model.Result;
import com.central.push.config.SseEmitterServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * https://blog.csdn.net/qq_39992641/article/details/105229674
 */
@RestController
@RequestMapping("/sse")
@Api(tags = "消息推送")
public class SseController {

    @ApiOperation(value = "创建连接")
    @GetMapping("/connect/{userId}")
    public SseEmitter connect(@PathVariable String userId) {
        return SseEmitterServer.connect(userId);
    }

    @ApiOperation(value = "群发消息")
    @GetMapping("/push/{message}")
    public Result<String> push(@PathVariable(name = "message") String message) {
        SseEmitterServer.batchSendMessage(message);
        return Result.succeed("成功推送消息给所有人");
    }

    @ApiOperation(value = "私发消息")
    @GetMapping("/pushOne/{messsage}/{userId}")
    public Result<String> pushOne(@PathVariable(name = "message") String message, @PathVariable(name = "userId") String userId) {
        SseEmitterServer.sendMessage(userId, message);
        return Result.succeed("消息推送成功");
    }

    @ApiOperation(value = "关闭连接")
    @GetMapping("/close/{userId}")
    public Result<String> close(@PathVariable("userId") String userId) {
        SseEmitterServer.removeUser(userId);
        return Result.succeed("连接关闭");
    }
}



