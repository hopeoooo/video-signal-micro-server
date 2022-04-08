package com.central.game.rabbitMq;

import com.alibaba.fastjson.JSONObject;
import com.central.common.model.PushResult;
import com.central.common.model.Result;
import com.central.game.constants.GameListEnum;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.game.service.IGameLotteryResultService;
import com.central.game.service.IPushGameDataToClientService;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import com.sun.corba.se.spi.ior.ObjectKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 百家乐开奖数据消费者
 */
@Component
@RabbitListener(queues = "DataCatch.resultQueue")
@Slf4j
public class GameLotterResultConsumer {

    @Autowired
    private IGameLotteryResultService gameLotteryResultService;
    @Autowired
    private PushService pushService;
    @Autowired
    private IPushGameDataToClientService pushGameDataToClientService;

    @RabbitHandler
    public void process(String data) {
        log.info("接收到开奖数据,data={}", data);
        saveData(data);
        log.info("开奖数据处理完成,data={}", data);
    }

    public void saveData(String data) {
        List<GameLotteryResultCo> list = new ArrayList<>();
        try {
            list = JSONObject.parseArray(data, GameLotteryResultCo.class);
        } catch (Exception e) {
            log.error("开奖数据解析失败,data={},msg={}", data, e.getMessage());
            return;
        }
        if (CollectionUtils.isEmpty(list)) {
            log.error("开奖数据解析结果为空,data={}", data);
            return;
        }
        GameLotteryResult result = null;
        //数据单条保存，防止批量保存时单条数据异常导致全部数据保存失败
        for (GameLotteryResultCo resultCo : list) {
            try {
                result = new GameLotteryResult();
                BeanUtils.copyProperties(resultCo, result);
                result.setGameId(GameListEnum.BACCARAT.getGameId().toString());
                result.setGameName(GameListEnum.BACCARAT.getGameName());
                result.setLotteryId(resultCo.getId());
                result.setLotteryTime(resultCo.getCreateTime());
                gameLotteryResultService.save(result);
                //推送开奖结果
                pushGameDataToClientService.pushLotterResult(result);
                //计算派彩，有效投注额，输赢
                gameLotteryResultService.calculateBetResult(result);
                //异步推送派彩结果
                pushGameDataToClientService.syncPushPayoutResult(result);
            } catch (Exception e) {
                log.error("开奖数据保存失败,data={},msg={}", result.toString(), e.getMessage());
            }
        }
    }
}
