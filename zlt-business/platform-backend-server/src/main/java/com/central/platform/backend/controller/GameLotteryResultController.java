package com.central.platform.backend.controller;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.game.feign.GameService;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.co.GameLotteryResultBackstageCo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/gameLotteryResult")
@Api(tags = "开奖记录")
public class GameLotteryResultController {

    @Autowired
    private GameService gameService;

    @ResponseBody
    @ApiOperation(value = "查询开奖结果")
    @GetMapping("/gameLotteryResult/findList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer")
    })
    public Result<PageResult<GameLotteryResult>> findList(@Valid @ModelAttribute GameLotteryResultBackstageCo params) {
        return gameService.findList(params);
    }

}
