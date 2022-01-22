package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.game.feign.GameService;
import com.central.game.model.GameList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@RestController
@RequestMapping("/gamelist")
@Api(tags = "游戏列表")
public class GameListController {

    @Autowired
    private GameService gameService;

    @ApiOperation(value = "新增/更新")
    @PostMapping("/save")
    public Result save(@RequestBody GameList gameList) {
        return gameService.save(gameList);
    }

    @ApiOperation(value = "根据ID删除")
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Long id) {
        return gameService.deleteById(id);
    }

    @ApiOperation(value = "查询全部游戏")
    @GetMapping("/findAll")
    public Result<List<GameList>> findAll() {
        return gameService.findAll();
    }

}
