package com.central.game.controller;

import com.alibaba.fastjson.JSONObject;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameLotteryResultBackstageCo;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.service.IGameLotteryResultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@RestController
@RequestMapping("/gameLotteryResult")
@Api(tags = "游戏下注记录")
public class GameLotteryResultController {

    @Autowired
    private IGameLotteryResultService gameLotteryResultService;
    @Autowired
    private RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @ApiOperation(value = "查询开奖结果")
    @GetMapping("/getLotteryResultList")
    public Result<List<GameLotteryResult>> getLotteryResultList(@ModelAttribute GameLotteryResultCo co) {
        List<GameLotteryResult> list = gameLotteryResultService.getLotteryResultList(co);
        return Result.succeed(list);
    }

    @ResponseBody
    @ApiOperation(value = "查询开奖结果-后台")
    @GetMapping("/findList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer")
    })
    public Result<PageResult<GameLotteryResult>> findList(@Valid @ModelAttribute GameLotteryResultBackstageCo params) {
        PageResult<GameLotteryResult> list = gameLotteryResultService.findList(params);
        return Result.succeed(list);
    }



    @GetMapping("/sendDirectMessage")
    @ApiOperation(value = "发送开奖结果MQ")
    public Result sendDirectMessage(@RequestParam("bureauNum") String bureauNum) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("tableNum", "1");
        map.put("bootNum", "1");
        map.put("bureauNum", bureauNum);
        map.put("createTime", "2022-03-25 20:38:55");
        map.put("id", 1);
        map.put("machineCode", "1");
        map.put("result", "189");
        map.put("resultName", "闲庄对大");
        list.add(map);
        list.add(map);
        rabbitTemplate.convertAndSend("DataCatch.resultQueue", JSONObject.toJSONString(list));
        return Result.succeed(list);
    }
}
