package com.central.game.rabbitMq;

import com.alibaba.fastjson.JSONObject;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.game.service.IGameLotteryResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 开奖数据消费者
 */
@Component
@RabbitListener(queues = "DataCatch.resultQueue")
@Slf4j
public class GameLotterResulyConsumer {

    @Autowired
    private IGameLotteryResultService gameLotteryResultService;

    @RabbitHandler
    public void process(String data) {
        log.info("接收到开奖数据,data={}", data);
        saveData(data);
        log.info("开奖数据处理完成,data={}", data);
    }

    public void saveData(String data) {
        List<GameLotteryResultCo> list = JSONObject.parseArray(data, GameLotteryResultCo.class);
        if (!CollectionUtils.isEmpty(list)) {
            GameLotteryResult result = null;
            //数据单条保存，防止批量保存时单条数据异常导致全部数据保存失败
            for (GameLotteryResultCo resultCo : list) {
                try {
                    result = new GameLotteryResult();
                    BeanUtils.copyProperties(resultCo, result);
                    result.setLotteryId(resultCo.getId());
                    result.setLotteryTime(resultCo.getCreateTime());
                    gameLotteryResultService.save(result);
                    gameLotteryResultService.calculateBetResult(result);
                } catch (Exception e) {
                    log.error("开奖数据保存失败,data={},msg={}", result.toString(), e.getMessage());
                }
            }
        }
    }
}
