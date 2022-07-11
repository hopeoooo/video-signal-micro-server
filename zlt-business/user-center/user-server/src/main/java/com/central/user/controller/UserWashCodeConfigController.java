package com.central.user.controller;

import cn.hutool.core.collection.CollUtil;
import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;
import com.central.game.feign.GameService;
import com.central.game.model.GameList;
import com.central.user.service.IUserWashCodeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@Api(tags = "个人洗码配置")
@RequestMapping("/userWashCode")
public class UserWashCodeConfigController {


    @Autowired
    private IUserWashCodeConfigService userWashCodeConfigService;

    @Resource
    private GameService gameService;


    @ApiOperation("查询个人洗码配置")
    @ResponseBody
    @GetMapping("/findUserWashCodeConfigList/{userId}")
    public Result<List<UserWashCodeConfig>> findUserWashCodeConfigList(@PathVariable Long userId) {
        List<UserWashCodeConfig> userWashCodeConfig = userWashCodeConfigService.findUserWashCodeConfigList(userId);
        if (CollUtil.isEmpty(userWashCodeConfig)) {
            //查询全局洗码配置
            Result<List<GameList>> gameList = gameService.findGameList(1);
            gameList.getDatas().forEach(info -> {
                UserWashCodeConfig cfg = new UserWashCodeConfig();
                cfg.setGameId(info.getId());
                cfg.setGameName(info.getName());
                cfg.setGameRate(info.getGameRate());
                userWashCodeConfig.add(cfg);
            });
        }
        return Result.succeed(userWashCodeConfig,"查询成功");
    }

    @ApiOperation("根据userId查询全部洗码配置")
    @GetMapping("/findWashCodeConfigListByUserId/{userId}")
    public Result<List<UserWashCodeConfig>> findWashCodeConfigListByUserId(@PathVariable Long userId) {
        List<UserWashCodeConfig> userWashCodeConfig = userWashCodeConfigService.findWashCodeConfigListByUserId(userId);
        return Result.succeed(userWashCodeConfig);
    }

    @ApiOperation("根据gameId和userId查询洗码配置")
    @GetMapping("/findWashCodeConfigListByGameIdAndUserId")
    public Result<List<UserWashCodeConfig>> findWashCodeConfigListByGameIdAndUserId(@RequestParam("gameId") Long gameId, @RequestParam("userId") Long userId) {
        List<UserWashCodeConfig> userWashCodeConfig = userWashCodeConfigService.findWashCodeConfigListByGameIdAndUserId(gameId, userId);
        return Result.succeed(userWashCodeConfig);
    }

    @ApiOperation(value = "保存")
    @PostMapping("/saveUserWashCodeConfig")
    public Result saveUserWashCodeConfig(@RequestBody List<UserWashCodeConfig> list) {
        return userWashCodeConfigService.saveCache(list);
    }
}
