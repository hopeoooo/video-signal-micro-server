package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.game.feign.GameService;
import com.central.game.model.GameList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = "洗码配置")
@Slf4j
@RequestMapping("/washCode/backend")
public class WashCodeConfigController {
    @Resource
    private GameService gameService;

    @ApiOperation("查询洗码配置")
    @ResponseBody
    @GetMapping("/gamelist/findAll")
    public Result<List<GameList>> findWashCodeConfigList() {
        return gameService.findAll();
    }

    @ApiOperation(value = "新增/更新")
    @PostMapping("/gamelist/save")
    public Result save(@RequestBody GameList gameList) {
        gameService.save(gameList);
        return Result.succeed();
    }
}
