package com.central.game.controller;

import com.central.common.model.Result;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.game.service.IGameLotteryResultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@RestController
@RequestMapping("/gameLotteryResult")
@Api(tags = "游戏下注记录")
public class GameLotteryResultController {

    @Autowired
    private IGameLotteryResultService gameLotteryResultService;

    @ApiOperation(value = "查询开奖结果")
    @GetMapping("/getLotteryResultList")
    public Result<List<GameLotteryResult>> getLotteryResultList(@ModelAttribute GameLotteryResultCo co) {
        List<GameLotteryResult> list = gameLotteryResultService.getLotteryResultList(co);
        return Result.succeed(list);
    }
}
