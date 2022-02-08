package com.rocketmq.demo.service;

public interface IMessageProvider {
    /**
     * 发送字符消息
     */
    void sendMethod(String msg);

    /**
     * 发送带tag的对象消息
     */
	<T> void sendMethodWithTags(T msg, String tag) ;
}
