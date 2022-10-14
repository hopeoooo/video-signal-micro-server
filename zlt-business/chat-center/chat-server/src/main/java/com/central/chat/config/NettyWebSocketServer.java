package com.central.chat.config;


import com.alibaba.fastjson.JSONObject;
import com.central.chat.service.ChatService;
import com.central.chat.vo.MessageVo;
import com.central.common.model.PushResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * https://blog.csdn.net/qq_38089964/article/details/81541846
 */
@Slf4j
@ServerEndpoint(path = "/ws/chat/{groupId}/{token}", host = "${ws.host}", port = "${ws.port}")
@Component
@Data
public class NettyWebSocketServer {

    @Autowired
    private ChatService chatService;
    @Autowired
    private CustomReactiveAuthentication customReactiveAuthentication;

    private static final Map<String, CopyOnWriteArraySet<NettyWebSocketServer>> groups = new HashMap<>();

    private Session session;

    private String groupId;

    private String userName;


    @OnOpen
    public void onOpen(Session session, @PathVariable String groupId, @PathVariable String token) {
        String userName = customReactiveAuthentication.authentication(token);
        if (ObjectUtils.isEmpty(userName)) {
            PushResult pushResult = PushResult.failed("认证失败");
            session.sendText(JSONObject.toJSONString(pushResult));
            session.close();
            log.error("/ws/chat/onOpen连接失败,获取用户信息失败,token={}",token);
            return;
        }
        this.session = session;
        this.groupId = groupId;
        this.userName = userName;
        CopyOnWriteArraySet<NettyWebSocketServer> friends = groups.get(groupId);
        if (friends == null) {
            synchronized (groups) {
                if (!groups.containsKey(groupId)) {
                    friends = new CopyOnWriteArraySet<>();
                    groups.put(groupId, friends);
                }
            }
        }
        //同名的后面的连接会覆盖前面的
        for (NettyWebSocketServer item : friends) {
            if (item.getUserName().equals(this.userName)) {
                friends.remove(item);
                break;
            }
        }
        friends.add(this);
        log.info("群组:{},用户:{} 加入连接，当前连接数为：{}", groupId, this.userName, friends.size());
        PushResult pushResult = PushResult.succeed("连接成功", "heartbeat", "客户端消息接收成功");
        session.sendText(JSONObject.toJSONString(pushResult));
    }

    @OnClose
    public void onClose() {
        CopyOnWriteArraySet<NettyWebSocketServer> friends = groups.get(groupId);
        if (friends != null) {
            friends.remove(this);
            log.info("群组:{},用户:{} 关闭连接，当前连接数为：{}", groupId, userName, friends.size());
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        CopyOnWriteArraySet<NettyWebSocketServer> friends = groups.get(groupId);
        if (friends != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = sdf.format(new Date());
            MessageVo vo = new MessageVo();
            vo.setUserName(this.userName);
            vo.setMessage(message);
            vo.setDateTime(dateTime);
            //异步保存用户聊天信息
            //chatService.syncSaveChatMessage(groupId, msg);
            for (NettyWebSocketServer item : friends) {
                PushResult pushResult = PushResult.succeed(vo, "message", "新消息");
                item.session.sendText(JSONObject.toJSONString(pushResult));
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("群组:{},用户:{}连接发生错误{}", groupId, userName, error.getMessage());
        error.printStackTrace();
    }

    /**
     * 通过房间ID群发消息
     *
     * @param groupId
     * @param message
     * @throws IOException
     */
    public static String sendMessageByGroupId(String groupId, String message) {
        message = filterStr(message);
        if(StringUtils.isBlank(message)){
            return "消息不合法";
        }
        CopyOnWriteArraySet<NettyWebSocketServer> friends = groups.get(groupId);
        if (friends == null) {
            return "消息推送失败,没有找到指定会话";
        }
        for (NettyWebSocketServer item : friends) {
            item.session.sendText(message);
        }
        return null;
    }

    public static Object getAllConnect() {
        return groups;
    }

    /**
     * 消息过滤
     * @param message
     * @return
     */
    private static String filterStr(String message){
        if("@HeartBeat@".equals(message)){
            return message;
        }
        StringBuilder buf = null;
        int len = message.length();
        for (int i = 0; i < len; i++) {
            char codePoint = message.charAt(i);
            if (!((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                    || (codePoint == 0xD)
                    || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                    || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                    || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))) {
                if (buf == null) {
                    buf = new StringBuilder(message.length());
                }
                buf.append(codePoint);
            }
        }
        if (buf == null) {
            return "";
        }
        return buf.toString();
    }
}

