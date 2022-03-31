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
@ServerEndpoint(path = "/ws/group/{groupId}/{userName}", host = "${ws.host}", port = "${ws.port}")
@Component
@Data
public class NettyWebSocketGroupServer {

    private static final Map<String, CopyOnWriteArraySet<NettyWebSocketGroupServer>> groups = new HashMap<>();

    private Session session;

    private String groupId;

    private String userName;


    @OnOpen
    public void onOpen(Session session, @PathVariable String groupId, @PathVariable String userName) {
        this.session = session;
        this.groupId = groupId;
        this.userName = userName;
        CopyOnWriteArraySet<NettyWebSocketGroupServer> friends = groups.get(this.groupId);
        if (friends == null) {
            synchronized (groups) {
                if (!groups.containsKey(this.groupId)) {
                    friends = new CopyOnWriteArraySet<>();
                    groups.put(this.groupId, friends);
                }
            }
        }
        //同名的后面的连接会覆盖前面的
        for (NettyWebSocketGroupServer item : friends) {
            if (item.getUserName().equals(userName)) {
                friends.remove(item);
                break;
            }
        }
        friends.add(this);
        log.info("群组:{},用户:{} 加入连接，当前连接数为：{}", groupId, userName, friends.size());
        onMessage(session, "连接成功");
    }

    @OnClose
    public void onClose() {
        CopyOnWriteArraySet<NettyWebSocketGroupServer> friends = groups.get(groupId);
        if (friends != null) {
            friends.remove(this);
            log.info("群组:{},用户:{} 关闭连接，当前连接数为：{}", groupId, userName, friends.size());
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("来自客户端的消息：{}", message);
        PushResult pushResult = PushResult.succeed(message, "heartbeat", "客户端消息接收成功");
        session.sendText(JSONObject.toJSONString(pushResult));
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("群组:{},用户:{}连接发生错误{}", groupId, userName, error.getMessage());
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
     * @param groupId
     * @param message
     * @throws IOException
     */
    public static String sendMessageByGroupId(String groupId, String message) {
        CopyOnWriteArraySet<NettyWebSocketGroupServer> friends = groups.get(groupId);
        if (friends == null) {
            return "消息推送失败,没有找到指定会话";
        }
        for (NettyWebSocketGroupServer item : friends) {
            item.session.sendText(message);
        }
        return null;
    }
}

