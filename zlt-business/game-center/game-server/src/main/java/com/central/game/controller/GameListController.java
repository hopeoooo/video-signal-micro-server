package com.central.game.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.central.common.model.PageResult;
import com.central.game.model.GameList;
import com.central.game.service.IGameListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import com.central.common.model.Result;
import com.central.common.model.SuperPage;

import java.math.BigDecimal;
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
    public Result<PageResult<GameList>> list(@ModelAttribute SuperPage superPage) {
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
        BigDecimal gameRate = gameList.getGameRate();
        if (gameRate.compareTo(new BigDecimal(0))==0 || gameRate.compareTo(new BigDecimal(100))>0){
            return  Result.failed("返水比例设置错误");
        }
        gameListService.saveOrUpdate(gameList);
        //通知客户端修改桌台状态
        gameListService.syncPushGameStatus(gameList);
        return Result.succeed();
    }

    @ApiOperation(value = "根据ID删除")
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Long id) {
        gameListService.removeById(id);
        return Result.succeed();
    }

    @ApiOperation(value = "查询全部游戏(前台)")
    @GetMapping("/findAll")
    public Result<List<GameList>> findAll() {
        List<GameList> list = gameListService.findAll();
        return Result.succeed(list);
    }

    @ApiOperation(value = "查询全部开启返水的游戏")
    @GetMapping("/findAllOpenRate")
    public Result<List<GameList>> findAllOpenRate() {
        List<GameList> list = gameListService.lambdaQuery().in(GameList::getRateStatus,1).list();
        return Result.succeed(list);
    }


    @ApiOperation(value = "查询洗码配置列表(后台)")
    @GetMapping("/findGameList")
    public Result<List<GameList>> findGameList(@RequestParam(value = "gameId", required = false) Long gameId, @RequestParam(value = "state", required = false) Integer state) {
        LambdaQueryWrapper<GameList> wrapper = new LambdaQueryWrapper<>();
        if (gameId != null) {
            wrapper.eq(GameList::getId, gameId);
        }
        if (state != null) {
            wrapper.eq(GameList::getRateStatus, state);
        }
        List<GameList> list = gameListService.list(wrapper);
        return Result.succeed(list);
    }

    @ApiOperation(value = "根据游戏ID查询开启洗码配置列表")
    @GetMapping("/findEnableGameListByGameId")
    public Result<List<GameList>> findEnableGameListByGameId(@RequestParam(value = "gameId", required = false) Long gameId, @RequestParam(value = "state", required = false) Integer state) {
        LambdaQueryWrapper<GameList> wrapper = new LambdaQueryWrapper<>();
        if (gameId != null) {
            wrapper.eq(GameList::getId, gameId);
        }
        if (state != null) {
            wrapper.eq(GameList::getRateStatus, state);
        }
        List<GameList> list = gameListService.list(wrapper);
        return Result.succeed(list);
    }
}
