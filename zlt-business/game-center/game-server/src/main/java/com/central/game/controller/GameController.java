package com.central.game.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 游戏模块
 */
@Slf4j
@RestController
@Api(tags = "游戏模块api")
@RequestMapping("/game")
public class GameController {

    @ApiOperation(value = "查询游戏列表")
    @GetMapping("/list")
    public String list(){
        return "test.game";
    }
}
