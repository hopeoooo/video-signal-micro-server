package com.central.oauth.service;

public interface ILogoutSuccessService {

    /**
     * 退出所有桌台
     * @param userName
     */
    void removeTableNumGroup(String userName);

    /**
     * 更新在线人数
     * @param num
     */
    void pushOnlineNum(Integer num);
}
