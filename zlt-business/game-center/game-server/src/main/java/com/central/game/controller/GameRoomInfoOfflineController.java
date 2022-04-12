package com.central.game.controller;

import com.alibaba.fastjson.JSONObject;
import com.central.common.model.Result;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.co.GameRoomInfoOfflineCo;
import com.central.game.model.vo.GameRoomListVo;
import com.central.game.service.IGameRoomInfoOfflineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@RestController
@RequestMapping("/gameRoomInfoOffline")
@Api(tags = "现场桌台信息详情")
public class GameRoomInfoOfflineController {

    @Autowired
    private IGameRoomInfoOfflineService gameRoomInfoOfflineService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @ApiOperation(value = "查询桌台最新动态信息")
    @GetMapping("/getTableInfo")
    public Result<GameRoomListVo> getTableInfo(@ModelAttribute GameRoomInfoOfflineCo co) {
        GameRoomListVo gameRoomListVo = gameRoomInfoOfflineService.getNewestTableInfoVo(co.getGameId(),co.getTableNum());
        return Result.succeed(gameRoomListVo);
    }

    @PostMapping("/sendDirectMessage")
    @ApiOperation(value = "发送桌台信息MQ")
    public Result sendDirectMessage(@RequestBody GameRoomInfoOffline info) {
        rabbitTemplate.convertAndSend("DataCatch.configQueue", JSONObject.toJSONString(info));
        return Result.succeed();
    }
}
