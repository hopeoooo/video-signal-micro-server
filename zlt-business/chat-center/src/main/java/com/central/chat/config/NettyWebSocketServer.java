package com.central.chat.config;


import com.alibaba.fastjson.JSONObject;
import com.central.common.model.PushResult;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * https://blog.csdn.net/qq_38089964/article/details/81541846
 */
@Slf4j
@ServerEndpoint(path = "/ws/chat/{roomId}/{userName}", host = "${ws.host}", port = "${ws.port}")
@Component
@Data
public class NettyWebSocketServer {

    private static final Map<String, CopyOnWriteArraySet<NettyWebSocketServer>> rooms = new HashMap<>();

    private Session session;

    private String roomId;

    private String userName;


    @OnOpen
    public void onOpen(Session session, @PathVariable String roomId, @PathVariable String userName) {
        this.session = session;
        this.roomId = roomId;
        this.userName = userName;
        CopyOnWriteArraySet<NettyWebSocketServer> friends = rooms.get(roomId);
        if (friends == null) {
            synchronized (rooms) {
                if (!rooms.containsKey(roomId)) {
                    friends = new CopyOnWriteArraySet<>();
                    rooms.put(roomId, friends);
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
        CopyOnWriteArraySet<NettyWebSocketServer> friends = rooms.get(roomId);
        if (friends != null) {
            friends.remove(this);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        CopyOnWriteArraySet<NettyWebSocketServer> friends = rooms.get(roomId);
        if (friends != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(new Date());
            //获取发送消息的用户名
            String userName = null;
            for (NettyWebSocketServer item : friends) {
                if (session == item.session) {
                    userName = item.getUserName();
                    break;
                }
            }
            Map<String, Object> data = new HashMap<>();
            data.put("userName", userName);
            data.put("message", message);
            data.put("date", date);
            String msg = JSONObject.toJSONString(data);
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
}

