package com.central.game.controller;

import com.central.common.model.Result;
import com.central.game.model.GamePlayConfig;
import com.central.game.service.IGamePlayConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@RestController
@RequestMapping("/gamePlayConfig")
@Api(tags = "游戏玩法配置")
public class GamePlayConfigController {
    @Autowired
    private IGamePlayConfigService gamePlayConfigService;


    @ApiOperation(value = "根据游戏ID查询玩法")
    @GetMapping("/findByGameId/{gameId}")
    public Result<List<GamePlayConfig>> findByGameId(@PathVariable("gameId") Long gameId) {
        List<GamePlayConfig> list = gamePlayConfigService.lambdaQuery().eq(GamePlayConfig::getGameId,gameId).eq(GamePlayConfig::getStatus,1).list();
        return Result.succeed(list);
    }

}
