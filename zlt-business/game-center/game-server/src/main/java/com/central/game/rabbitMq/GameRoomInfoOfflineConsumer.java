package com.central.game.rabbitMq;

import com.alibaba.fastjson.JSONObject;
import com.central.common.model.PushResult;
import com.central.common.model.Result;
import com.central.game.constants.GameListEnum;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.service.IGameRoomInfoOfflineService;
import com.central.game.service.IPushGameDataToClientService;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 百家乐桌台信息详情
 */
@Component
@RabbitListener(queues = "DataCatch.configQueue")
@Slf4j
public class GameRoomInfoOfflineConsumer {

    @Autowired
    private IGameRoomInfoOfflineService gameRoomInfoOfflineService;
    @Autowired
    private IPushGameDataToClientService pushGameDataToClientService;

    @RabbitHandler
    public void process(String data) {
        log.info("接收到桌台配置详情数据,data={}", data);
        saveOrUpdateData(data);
        log.info("桌台配置详细信息处理完成,data={}", data);
    }

    public void saveOrUpdateData(String data) {
        GameRoomInfoOffline po = null;
        try {
            po = JSONObject.parseObject(data, GameRoomInfoOffline.class);
        } catch (Exception e) {
            log.error("桌台配置信息数据解析失败,data={},msg={}", data, e.getMessage());
            return;
        }
        if (po == null) {
            log.error("桌台配置信息数据解析结果为空,data={}", data);
            return;
        }
        po.setGameId(GameListEnum.BACCARAT.getGameId());
        po.setGameName(GameListEnum.BACCARAT.getGameName());
        GameRoomInfoOffline detailOffline = gameRoomInfoOfflineService.lambdaQuery().eq(GameRoomInfoOffline::getMachineCode, po.getMachineCode()).eq(GameRoomInfoOffline::getTableNum, po.getTableNum())
                .eq(GameRoomInfoOffline::getBootNum, po.getBootNum()).eq(GameRoomInfoOffline::getBureauNum, po.getBureauNum()).one();
        if (detailOffline == null) {
            if (po.getTimes() != null && po.getStatus() == 1) {
                Date date = new Date(po.getTimes());
                po.setStartTime(date);
            }
            gameRoomInfoOfflineService.save(po);
        } else {
            po.setId(detailOffline.getId());
            po.setCreateTime(detailOffline.getCreateTime());
            po.setUpdateTime(detailOffline.getUpdateTime());
            if (po.getTimes() != null && po.getStatus() == 4) {
                Date date = new Date(po.getTimes());
                po.setEndTime(date);
            }
            BeanUtils.copyProperties(po, detailOffline);
            gameRoomInfoOfflineService.updateById(detailOffline);
        }
        log.info("桌台配置信息数据更新完成，data={}", data);
        //推送客户端消息
        //计算实时倒计时
        if (po != null && po.getTimes() != null && po.getSecond() != null) {
            long second = (System.currentTimeMillis() - po.getTimes()) / 1000;
            long differ = po.getSecond() - second;
            Long currentSecond = differ > 0 ? differ : 0;
            po.setCurrentSecond(currentSecond.intValue());
        }
        pushGameDataToClientService.syncPushGameRoomInfo(po);
    }
}
