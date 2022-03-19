package com.central.push.config;


import com.alibaba.fastjson.JSONObject;
import com.central.common.model.PushResult;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * https://blog.csdn.net/qq_38089964/article/details/81541846
 */
@Slf4j
@ServerEndpoint(path = "/ws/room/{roomId}/{userName}", host = "${ws.host}", port = "${ws.port}")
@Component
@Data
public class NettyWebSocketRoomServer {

    private static final Map<String, CopyOnWriteArraySet<NettyWebSocketRoomServer>> rooms = new HashMap<>();

    private Session session;

    private String roomId;

    private String userName;


    @OnOpen
    public void onOpen(Session session, @PathVariable String roomId, @PathVariable String userName) {
        this.session = session;
        this.roomId = roomId;
        this.userName = userName;
        CopyOnWriteArraySet<NettyWebSocketRoomServer> friends = rooms.get(roomId);
        if (friends == null) {
            synchronized (rooms) {
                if (!rooms.containsKey(roomId)) {
                    friends = new CopyOnWriteArraySet<>();
                    rooms.put(roomId, friends);
                }
            }
        }
        //同名的后面的连接会覆盖前面的
        for (NettyWebSocketRoomServer item : friends) {
            if (item.getUserName().equals(userName)) {
                friends.remove(item);
                break;
            }
        }
        friends.add(this);
    }

    @OnClose
    public void onClose() {
        CopyOnWriteArraySet<NettyWebSocketRoomServer> friends = rooms.get(roomId);
        if (friends != null) {
            friends.remove(this);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息：{}", message);
        PushResult pushResult = PushResult.succeed(message, "heartbeat", "客户端消息接收成功");
        session.sendText(JSONObject.toJSONString(pushResult));
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("发生错误{}", error.getMessage());
        error.printStackTrace();
    }

    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
        for (byte b : bytes) {
            System.out.println(b);
        }
        session.sendBinary(bytes);
    }

    @OnEvent
    public void onEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("read idle");
                    break;
                case WRITER_IDLE:
                    System.out.println("write idle");
                    break;
                case ALL_IDLE:
                    System.out.println("all idle");
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 通过房间ID群发消息
     *
     * @param roomId
     * @param message
     * @throws IOException
     */
    public static String sendMessageByRoomId(String roomId, String message) {
        CopyOnWriteArraySet<NettyWebSocketRoomServer> friends = rooms.get(roomId);
        if (friends == null) {
            return "消息推送失败,没有找到指定会话";
        }
        for (NettyWebSocketRoomServer item : friends) {
            item.session.sendText(message);
        }
        return null;
    }
}

