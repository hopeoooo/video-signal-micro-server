package com.central.push.controller;

import com.central.common.model.Result;
import com.central.push.config.WebSocketServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ws/api")
public class WebSocketController {


    /**
     * 群发消息内容
     *
     * @param message
     * @return
     */
    @GetMapping(value = "/sendAll/{message}")
    public Result sendAllMessage(@PathVariable(name = "message") String message) throws Exception {
        WebSocketServer.batchSendMessage(message);
        return Result.succeed("群消息发送成功");
    }

    /**
     * 指定用户发消息
     *
     * @param message  消息内容
     * @param userName 用户名
     * @return
     */
    @GetMapping(value = "/sendOne/{userName}/{message}")
    public Result sendOneMessage(@PathVariable(name = "userName") String userName, @PathVariable(name = "message") String message) throws Exception {
        String msg = WebSocketServer.sendOneMessage(message, userName);
        if(StringUtils.isBlank(msg)){
            return Result.succeed(userName + "消息推送成功");
        }
        return Result.failed(msg);
    }
}