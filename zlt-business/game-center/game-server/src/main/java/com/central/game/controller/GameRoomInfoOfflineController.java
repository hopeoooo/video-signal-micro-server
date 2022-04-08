package com.central.game.controller;

import com.alibaba.fastjson.JSONObject;
import com.central.common.model.Result;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.co.GameRecordLivePotCo;
import com.central.game.model.co.GameRoomInfoOfflineCo;
import com.central.game.model.vo.LivePotVo;
import com.central.game.service.IGameRoomInfoOfflineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Result<GameRoomInfoOffline> getTableInfo(@ModelAttribute GameRoomInfoOfflineCo co) {
        GameRoomInfoOffline infoOffline = gameRoomInfoOfflineService.getNewestTableInfo(co.getGameId(),co.getTableNum());
        //计算实时倒计时
        if (infoOffline != null && infoOffline.getTimes() != null && infoOffline.getSecond() != null) {
            //数据修改后距离当前时间过了多少秒
            long second = (System.currentTimeMillis() - infoOffline.getTimes()) / 1000;
            long differ = infoOffline.getSecond() - second;
            Long currentSecond = differ > 0 ? differ : 0;
            infoOffline.setCurrentSecond(currentSecond.intValue());
        }
        return Result.succeed(infoOffline);
    }

    @PostMapping("/sendDirectMessage")
    @ApiOperation(value = "发送桌台信息MQ")
    public Result sendDirectMessage(@RequestBody GameRoomInfoOffline info) {
        rabbitTemplate.convertAndSend("DataCatch.configQueue", JSONObject.toJSONString(info));
        return Result.succeed();
    }
}
