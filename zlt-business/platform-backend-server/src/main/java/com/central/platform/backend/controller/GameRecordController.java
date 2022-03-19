package com.central.platform.backend.controller;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.game.dto.GameRecordDto;
import com.central.game.feign.GameService;
import com.central.game.model.GameRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/gameRecord")
@Api(tags = "投注记录")
public class GameRecordController {

    @Autowired
    private GameService gameService;


    /**
     * 分页查询游戏下注数据
     *
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "总投注记录")
    @GetMapping("/findList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "gameId", value = "游戏Id", required = false, dataType = "Long"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, dataType = "Long"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "Long"),
    })
    public Result<PageResult<GameRecord>> findList(@RequestParam Map<String, Object> params) {
        return gameService.findList(params);
    }



    @ResponseBody
    @ApiOperation(value = "总投注记录-总计")
    @GetMapping("/findGameRecordTotal")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameId", value = "游戏Id", required = false, dataType = "Long"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, dataType = "Long"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "Long"),
    })
    public Result<GameRecordDto> findGameRecordTotal(@RequestParam Map<String, Object> params) {
        return gameService.findGameRecordTotal(params);
    }

}
