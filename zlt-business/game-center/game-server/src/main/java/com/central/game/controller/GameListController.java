package com.central.game.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.PageResult2;
import com.central.game.model.GameList;
import com.central.game.service.IGameListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SuperPage;

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
    private IGameListService gameListService;

    @ApiOperation(value = "分页查询列表")
    @GetMapping("/list")
    public Result<PageResult2<GameList>> list(@ModelAttribute SuperPage superPage) {
        return Result.succeed(gameListService.findList(superPage));
    }

    @ApiOperation(value = "根据ID查询")
    @GetMapping("/findById/{id}")
    public Result<GameList> findById(@PathVariable Long id) {
        GameList model = gameListService.getById(id);
        return Result.succeed(model);
    }

    @ApiOperation(value = "新增/更新")
    @PostMapping("/save")
    public Result save(@RequestBody GameList gameList) {
        gameListService.saveOrUpdate(gameList);
        return Result.succeed();
    }

    @ApiOperation(value = "根据ID删除")
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Long id) {
        gameListService.removeById(id);
        return Result.succeed();
    }

    @ApiOperation(value = "查询全部游戏")
    @GetMapping("/findAll")
    public Result<List<GameList>> findAll() {
        List<GameList> list = gameListService.lambdaQuery().in(GameList::getGameStatus,1,2).list();
        return Result.succeed(list);
    }

    @ApiOperation(value = "查询全部开启返水的游戏")
    @GetMapping("/findAllOpenRate")
    public Result<List<GameList>> findAllOpenRate() {
        List<GameList> list = gameListService.lambdaQuery().in(GameList::getRateStatus,1).list();
        return Result.succeed(list);
    }
}
