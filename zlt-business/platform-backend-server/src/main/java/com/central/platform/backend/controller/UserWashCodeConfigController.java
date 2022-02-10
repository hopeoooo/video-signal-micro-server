package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;
import com.central.game.feign.GameService;
import com.central.game.model.GameList;
import com.central.user.feign.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = "个人洗码配置")
@Slf4j
@RequestMapping("/userWashCode/backend")
public class UserWashCodeConfigController {

    @Resource
    private UserService userService;
    @Resource
    private GameService gameService;

    @ApiOperation("查询个人洗码配置")
    @ResponseBody
    @GetMapping("/userWashCode/findUserWashCodeConfigList/{userId}")
    public Result<List<UserWashCodeConfig>> findUserWashCodeConfigList(@PathVariable Long userId) {
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
            userWashCodeConfig.setGameRate(info.getGameRate());
            list.add(userWashCodeConfig);
        });
        return  Result.succeed(list,"查询成功");
    }


    @ApiOperation(value = "保存")
    @PostMapping("/userWashCode/saveUserWashCodeConfig")
    public Result saveUserWashCodeConfig(@RequestBody List<UserWashCodeConfig> list) {
        return userService.saveUserWashCodeConfig(list);
    }
}
