package com.central.platform.backend.controller;

import cn.hutool.core.collection.CollUtil;
import com.central.common.model.Result;
import com.central.game.model.co.GameListCo;
import com.central.game.feign.GameService;
import com.central.game.model.GameList;
import com.central.platform.backend.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

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
    public Result save(@RequestBody GameListCo gameList) {
        return gameService.save(gameList);
    }

    @ApiOperation(value = "根据ID删除")
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Long id) {
        return gameService.deleteById(id);
    }

    @ApiOperation(value = "根据ID查询")
    @GetMapping("/findById/{id}")
    public Result findById(@PathVariable Long id) {
        Result<GameList> byId = gameService.findById(id);
        if (byId.getResp_code() == 0 && Objects.nonNull(byId.getDatas())) {
            GameList gameList = byId.getDatas();
            if (gameList.getGameStatus() == 2 && Objects.nonNull(gameList.getMaintainEnd())
                && new Date().compareTo(gameList.getMaintainEnd()) > 0) {
                Map<String, Object> params = new HashMap<>();
                params.put("time", DateUtil.getTimeString(new Date()));
                params.put("gameStatus", 2);
                gameService.updateGameStatus(params);
                gameList.setGameStatus(1);
            }
            return Result.succeed(gameList);
        }
        return byId;
    }

    @ApiOperation(value = "查询全部游戏")
    @GetMapping("/gamelist/findGameList")
    public Result<List<GameList>> findGameList(@RequestParam(value = "state", required = false) Integer state) {
        Result<List<GameList>> result = gameService.findGameList(state);
        if (result.getResp_code() == 0 && CollUtil.isNotEmpty(result.getDatas())) {
            Boolean tag = false;
            List<GameList> datas = result.getDatas();
            for (GameList gameList:datas){
                if (gameList.getGameStatus() == 2 && Objects.nonNull(gameList.getMaintainEnd())
                    && new Date().compareTo(gameList.getMaintainEnd()) > 0) {
                    tag = true;
                    gameList.setGameStatus(1);
                }
            }
            if (tag) {
                Map<String, Object> params = new HashMap<>();
                params.put("time", DateUtil.getTimeString(new Date()));
                params.put("gameStatus", 2);
                gameService.updateGameStatus(params);
            }
            return Result.succeed(datas);
        }
        return result;
    }

}
