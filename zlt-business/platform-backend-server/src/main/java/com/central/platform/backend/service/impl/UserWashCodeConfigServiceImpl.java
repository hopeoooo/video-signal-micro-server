package com.central.platform.backend.service.impl;

import com.central.common.feign.UserService;
import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;
import com.central.game.feign.GameService;
import com.central.game.model.GameList;
import com.central.platform.backend.service.IUserWashCodeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class UserWashCodeConfigServiceImpl implements IUserWashCodeConfigService {

    @Resource
    private UserService userService;
    @Resource
    private GameService gameService;

    @Override
    public Result<List<UserWashCodeConfig>> findUserWashCodeConfigList(Long userId) {
        Result<List<UserWashCodeConfig>> userWashCodeConfigList = userService.findUserWashCodeConfigList(userId);
        if (userWashCodeConfigList.getDatas().size()>0) {
            return userWashCodeConfigList;
        }
        //查询全局洗码配置
        List<UserWashCodeConfig> list = new ArrayList<>();
        Result<List<GameList>> gameList = gameService.findGameList(1);
        gameList.getDatas().forEach(info -> {
            UserWashCodeConfig userWashCodeConfig = new UserWashCodeConfig();
            userWashCodeConfig.setGameId(info.getId());
            userWashCodeConfig.setGameName(info.getName());
            list.add(userWashCodeConfig);
        });
        return  Result.succeed(list,"查询成功");
    }

    @Override
    public Result saveUserWashCodeConfig( List<UserWashCodeConfig> list) {
        return userService.saveUserWashCodeConfig(list);
    }
}
