package com.central.oauth.service.impl;

import com.central.game.feign.GameService;
import com.central.oauth.service.ILogoutSuccessService;
import com.central.user.feign.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogoutSuccessServiceImpl implements ILogoutSuccessService {

    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;

    @Override
    public void removeTableNumGroup(String userName) {
        gameService.removeTableNumGroup(userName);
    }

    @Override
    public void pushOnlineNum(Integer num) {
        userService.pushOnlineNum(num);
    }
}
