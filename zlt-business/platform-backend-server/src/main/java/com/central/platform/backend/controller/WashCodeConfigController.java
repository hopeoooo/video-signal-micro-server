package com.central.platform.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.central.common.model.Result;
import com.central.game.feign.GameService;
import com.central.game.model.GameList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@RestController
@Api(tags = "洗码配置")
@Slf4j
@RequestMapping("/washCode/backend")
public class WashCodeConfigController {
    @Resource
    private GameService gameService;

    @ApiOperation(value = "查询洗码配置")
    @GetMapping("/gamelist/findGameList")
    public Result<List<GameList>> findGameList( @RequestParam(value = "state", required = false) Integer state) {
        return  gameService.findGameList(state);
    }


    @ApiOperation(value = "新增/更新")
    @PostMapping("/gamelist/save")
    public Result save(@RequestBody GameList gameList) {
        BigDecimal gameRate = gameList.getGameRate();
        if (gameRate.compareTo(new BigDecimal(0))==0 || gameRate.compareTo(new BigDecimal(100))>0){
            return  Result.failed("返水比例设置错误");
        }
        gameService.save(gameList);
        return Result.succeed();
    }
}
