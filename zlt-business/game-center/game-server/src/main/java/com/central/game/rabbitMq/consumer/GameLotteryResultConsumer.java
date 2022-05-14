package com.central.game.rabbitMq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.central.game.constants.GameListEnum;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.game.service.IGameLotteryResultService;
import com.central.game.service.IGameRecordService;
import com.central.game.service.IPushGameDataToClientService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 百家乐开奖数据消费者
 */
@Component
@RabbitListener(queues = "DataCatch.resultQueue")
@Slf4j
public class GameLotteryResultConsumer {

    @Autowired
    private IGameLotteryResultService gameLotteryResultService;
    @Autowired
    private IGameRecordService gameRecordService;
    @Autowired
    private IPushGameDataToClientService pushGameDataToClientService;

    @RabbitHandler
    public void process(String data, Channel channel, Message message) throws IOException {
        log.info("接收到开奖数据,data={}", data);
        saveData(data);
        // 消息的标识，false只确认当前一个消息收到，true确认所有consumer获得的消息（成功消费，消息从队列中删除 ）
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
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
                result.setGameId(GameListEnum.BACCARAT.getGameId());
                result.setGameName(GameListEnum.BACCARAT.getGameName());
                result.setLotteryId(resultCo.getId());
                result.setLotteryTime(resultCo.getCreateTime());
                gameLotteryResultService.save(result);
                //业务处理
                business(result);
            } catch (Exception e) {
                log.error("开奖数据保存失败,data={},msg={}", result.toString(), e.getMessage());
            }

        }
    }

    /**
     * 业务处理
     * @param result
     */
    public void business(GameLotteryResult result){
        try {
            //按靴缓存数据，方便列表统计查询
            gameLotteryResultService.syncSaveToRedis(result);
            //推送开奖结果
            pushGameDataToClientService.pushLotterResult(result);
            //计算派彩，有效投注额，输赢
            gameLotteryResultService.calculateBetResult(result);
            //异步推送派彩结果
            pushGameDataToClientService.syncPushPayoutResult(result);
            //异步删除游客用户记录
            gameRecordService.syncDeleteGuestRecordBureauNum(result.getGameId(),result.getTableNum(),result.getBootNum(),result.getBureauNum());
        }catch (Exception e){
            log.error("开奖数据业务处理失败,data={},msg={}", result.toString(), e.getMessage());
        }
    }
}
