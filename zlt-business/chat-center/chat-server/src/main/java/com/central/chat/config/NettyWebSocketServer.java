package com.central.chat.config;


import com.alibaba.fastjson.JSONObject;
import com.central.chat.service.ChatService;
import com.central.chat.vo.MessageVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
@ServerEndpoint(path = "/ws/chat/{groupId}/{userName}", host = "${ws.host}", port = "${ws.port}")
@Component
@Data
public class NettyWebSocketServer {

    @Autowired
    private ChatService chatService;

    private static final Map<String, CopyOnWriteArraySet<NettyWebSocketServer>> groups = new HashMap<>();

    private Session session;

    private String groupId;

    private String userName;


    @OnOpen
    public void onOpen(Session session, @PathVariable String groupId, @PathVariable String userName) {
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
            if (item.getUserName().equals(userName)) {
                friends.remove(item);
                break;
            }
        }
        friends.add(this);
    }

    @OnClose
    public void onClose() {
        CopyOnWriteArraySet<NettyWebSocketServer> friends = groups.get(groupId);
        if (friends != null) {
            friends.remove(this);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        CopyOnWriteArraySet<NettyWebSocketServer> friends = groups.get(groupId);
        if (friends != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = sdf.format(new Date());
            MessageVo vo = new MessageVo();
            vo.setUserName(this.userName);
            vo.setMessage(message);
            vo.setDateTime(dateTime);
            String msg = JSONObject.toJSONString(vo);
            //异步保存用户聊天信息
            //chatService.syncSaveChatMessage(groupId, msg);
            for (NettyWebSocketServer item : friends) {
                item.session.sendText(msg);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("发生错误{}", error.getMessage());
        error.printStackTrace();
    }

    public static void main(String[] args) {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", 1);
        data.put("message", 1);
        data.put("date", 1);
        System.out.println(JSONObject.toJSONString(data));
    }

    /**
     * 通过房间ID群发消息
     *
     * @param groupId
     * @param message
     * @throws IOException
     */
    public static String sendMessageByGroupId(String groupId, String message) {
        CopyOnWriteArraySet<NettyWebSocketServer> friends = groups.get(groupId);
        if (friends == null) {
            return "消息推送失败,没有找到指定会话";
        }
        for (NettyWebSocketServer item : friends) {
            item.session.sendText(message);
        }
        return null;
    }
}

