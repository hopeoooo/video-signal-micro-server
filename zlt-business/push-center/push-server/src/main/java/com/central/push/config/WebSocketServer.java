package com.central.push.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ws://localhost:9900/api-push/ws/asset?admin     admin 为用户名
 */
@ServerEndpoint(value = "/ws/asset")
@Component
@Slf4j
public class WebSocketServer {

    // concurrent包的线程安全map，用来存放每个客户端对应的Session对象。
    private static Map<String, Session> connectSession = new ConcurrentHashMap<>();


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        String userName = session.getQueryString();
        if (StringUtils.isBlank(userName)) {
            throw new IOException("userName不能为空");
        }
        connectSession.put(userName, session);
        log.info("有连接加入，当前连接数为：{}", connectSession.size());
        SendMessage(session, "连接成功");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        String userName = session.getQueryString();
        connectSession.remove(userName);
        log.info("有连接关闭，当前连接数为：{}", connectSession.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息：{}", message);
        SendMessage(session, "收到消息，消息内容：" + message);

    }

    /**
     * 出现错误
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误：{}，Session ID： {}", error.getMessage(), session.getId());
        error.printStackTrace();
    }

    /**
     * 发送消息，实践表明，每次浏览器刷新，session会发生变化。
     *
     * @param session
     * @param message
     */
    public static void SendMessage(Session session, String message) {
        try {
//            session.getBasicRemote().sendText(String.format("%s (From Server，Session ID=%s)",message,session.getId()));
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("发送消息出错：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 群发消息
     *
     * @param message
     * @throws IOException
     */
    public static void batchSendMessage(String message) throws Exception {
        for (Session session : connectSession.values()) {
            if (session.isOpen()) {
                SendMessage(session, message);
            }
        }
    }

    /**
     * 指定userName发送消息
     *
     * @param userName
     * @param message
     * @throws IOException
     */
    public static String sendOneMessage(String message, String userName) throws Exception {
        Session session = null;
        for (Session s : connectSession.values()) {
            if (s.getQueryString().equals(userName)) {
                session = s;
                break;
            }
        }
        if (session != null) {
            SendMessage(session, message);
            return null;
        }
        log.error("没有找到指定ID的会话：{}", userName);
        return "消息推送失败,没有找到指定会话";
    }
}
