package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.game.feign.GameService;
import com.central.game.model.GameList;
import com.central.game.model.co.GameListCo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "洗码配置")
@Slf4j
@RequestMapping("/game")
public class WashCodeConfigController {
    @Resource
    private GameService gameService;

    @ApiOperation(value = "查询洗码配置")
    @GetMapping("/washCode/backend/gamelist/findGameList")
    public Result<List<GameList>> findGameList( @RequestParam(value = "state", required = false) Integer state) {
        return  gameService.findGameList(state);
    }


    @ApiOperation(value = "新增/更新")
    @PostMapping("/washCode/backend/gamelist/save")
    public Result save(@Valid @RequestBody GameListCo gameList) {
        return gameService.save(gameList);
    }
}
