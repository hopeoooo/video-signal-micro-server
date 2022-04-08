package com.central.game.service.impl;

import com.central.common.model.PushResult;
import com.central.common.model.Result;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRecord;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.vo.LivePotVo;
import com.central.game.model.vo.LotteryResultVo;
import com.central.game.model.vo.NewAddLivePotVo;
import com.central.game.service.IGameRecordService;
import com.central.game.service.IPushGameDataToClientService;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PushGameDataToClientServiceImpl implements IPushGameDataToClientService {

    @Autowired
    private PushService pushService;
    @Autowired
    @Lazy
    private IGameRecordService gameRecordService;

    /**
     * 推送新增的投注数据
     *
     * @param gameId
     * @param tableNum
     * @param bootNum
     * @param bureauNum
     * @param newAddBetList
     */
    @Override
    @Async
    public void syncLivePot(Long gameId, String tableNum, String bootNum, String bureauNum, List<LivePotVo> newAddBetList) {
        if (CollectionUtils.isEmpty(newAddBetList)) {
            return;
        }
        String groupId = gameId + "-" + tableNum;
        NewAddLivePotVo newAddLivePotVo = new NewAddLivePotVo();
        newAddLivePotVo.setGameId(gameId);
        newAddLivePotVo.setTableNum(tableNum);
        newAddLivePotVo.setBootNum(bootNum);
        newAddLivePotVo.setBureauNum(bureauNum);
        newAddLivePotVo.setBetResult(newAddBetList);
        PushResult<NewAddLivePotVo> pushResult = PushResult.succeed(newAddLivePotVo, SocketTypeConstant.LIVE_POT, "即时彩池数据送成功");
        Result<String> push = pushService.sendMessageByGroupId(groupId, com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("即时彩池数据推送结果:groupId={},result={}", groupId, push);
    }


    @Override
    @Async
    public void syncPushPayoutResult(GameLotteryResult result) {
        String groupId = result.getGameId() + "-" + result.getTableNum();
        List<GameRecord> payoutResult = gameRecordService.getPayoutResult(result.getGameId(), result.getTableNum(), result.getBootNum(), result.getBureauNum());
        for (GameRecord gameRecord : payoutResult) {
            PushResult<GameRecord> pushResult = PushResult.succeed(gameRecord, SocketTypeConstant.PAYOUT_RESULT, "派彩结果信息推送成功");
            Result<String> push = pushService.sendMessageByGroupIdAndUserName(groupId, gameRecord.getUserName(), com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
            log.info("派彩结果信息推送结果:groupId={},userName={},result={}", groupId, gameRecord.getUserName(), push);
        }
    }

    @Override
    @Async
    public void pushLotterResult(GameLotteryResult result) {
        String groupId = result.getGameId() + "-" + result.getTableNum();
        LotteryResultVo vo = new LotteryResultVo();
        vo.setBetCodes(result.getResult());
        vo.setBetNames(result.getResultName());
        PushResult<LotteryResultVo> pushResult = PushResult.succeed(vo, SocketTypeConstant.LOTTER_RESULT, "桌台开奖信息推送成功");
        Result<String> push = pushService.sendMessageByGroupId(groupId, com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("桌台开奖信息推送结果:groupId={},result={}", groupId, push);
    }

    @Override
    @Async
    public void syncPushGameRoomInfo(GameRoomInfoOffline po) {
        String groupId = po.getGameId() + "-" + po.getTableNum();
        PushResult<GameRoomInfoOffline> pushResult = PushResult.succeed(po, SocketTypeConstant.TABLE_INFO, "桌台配置信息推送成功");
        Result<String> push = pushService.sendMessageByGroupId(groupId, com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("桌台配置信息推送结果:groupId={},result={}", groupId, push);
    }
}
