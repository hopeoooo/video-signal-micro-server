package com.central.game.service.impl;

import com.central.common.model.CodeEnum;
import com.central.common.model.PushResult;
import com.central.common.model.Result;
import com.central.common.redis.template.RedisRepository;
import com.central.common.utils.BigDecimalUtils;
import com.central.game.model.GameList;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.GameRoomList;
import com.central.game.model.vo.*;
import com.central.game.service.IGameRecordService;
import com.central.game.service.IPushGameDataToClientService;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import com.central.user.feign.UserService;
import com.central.user.model.vo.SysUserInfoMoneyVo;
import com.central.user.model.vo.SysUserMoneyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PushGameDataToClientServiceImpl implements IPushGameDataToClientService {

    @Autowired
    private PushService pushService;
    @Autowired
    private UserService userService;
    @Autowired
    @Lazy
    private IGameRecordService gameRecordService;

    /**
     * 推送新增的投注数据
     */
    @Override
    @Async
    public void syncLivePot(NewAddLivePotVo newAddLivePotVo) {
        if (newAddLivePotVo == null) {
            return;
        }
        String groupId = newAddLivePotVo.getGameId() + "-" + newAddLivePotVo.getTableNum();
        PushResult<NewAddLivePotVo> pushResult = PushResult.succeed(newAddLivePotVo, SocketTypeConstant.LIVE_POT, "即时彩池数据送成功");
        Result<String> push = pushService.sendMessageByGroupId(groupId, com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("下注界面即时彩池数据推送结果:groupId={},result={}", groupId, push);
        //推送大厅桌台数据变化
        Result<String> hallPush = pushService.sendAllMessage(com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("大厅即时彩池数据推送结果:result={}", hallPush);
    }


    @Override
//    @Async
    public void syncPushPayoutResult(GameLotteryResult result) {
        String groupId = result.getGameId() + "-" + result.getTableNum();
        List<PayoutResultVo> payoutResult = gameRecordService.getPayoutResult(result.getGameId(), result.getTableNum(), result.getBootNum(), result.getBureauNum());
        for (PayoutResultVo payoutResultVo : payoutResult) {
            payoutResultVo = BigDecimalUtils.keepDecimal(payoutResultVo);
            PushResult<PayoutResultVo> pushResult = PushResult.succeed(payoutResultVo, SocketTypeConstant.PAYOUT_RESULT, "派彩结果信息推送成功");
            Result<String> push = pushService.sendMessageByGroupIdAndUserName(groupId, payoutResultVo.getUserName(), com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
            log.info("派彩结果信息推送结果:groupId={},userName={},result={}", groupId, payoutResultVo.getUserName(), push);
        }
    }

    @Override
//    @Async
    public void pushLotterResult(GameLotteryResult result) {
        String groupId = result.getGameId() + "-" + result.getTableNum();
        LotteryResultVo vo = new LotteryResultVo();
        BeanUtils.copyProperties(result,vo);
        vo.setBetCodes(result.getResult());
        vo.setBetNames(result.getResultName());
        PushResult<LotteryResultVo> pushResult = PushResult.succeed(vo, SocketTypeConstant.LOTTERY_RESULT, "桌台开奖信息推送成功");
        Result<String> push = pushService.sendMessageByGroupId(groupId, com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("下注接口桌台开奖信息推送结果:groupId={},result={}", groupId, push);
        Result<String> hallPush = pushService.sendAllMessage(com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("大厅桌台开奖信息推送结果:result={}", hallPush);
    }

    @Override
//    @Async
    public void syncPushGameRoomInfo(GameRoomInfoOffline po) {
        String groupId = po.getGameId() + "-" + po.getTableNum();
        PushResult<GameRoomInfoOffline> pushResult = PushResult.succeed(po, SocketTypeConstant.TABLE_INFO, "桌台配置信息推送成功");
        //推送下注界面
        Result<String> push = pushService.sendMessageByGroupId(groupId, com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("下注界面桌台配置信息推送结果:groupId={},result={}", groupId, push);
        //推送大厅
        Result<String> hallPush = pushService.sendAllMessage(com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("大厅桌台配置信息推送结果,result={}", hallPush);
    }

    @Override
    @Async
    public void syncPushGameRoomStatus(GameRoomList po) {
        //推送大厅
        PushResult<GameRoomList> pushResult = PushResult.succeed(po, SocketTypeConstant.UPDATE_TABLE_STATUS, "后台修改桌台状态信息推送成功");
        Result<String> hallPush = pushService.sendAllMessage(com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("大厅桌台配置信息推送结果,result={}", hallPush);
    }

    @Override
    @Async
    public void syncTableNumGroup(NewAddLivePotVo newAddLivePotVo) {
        List<LivePotVo> betResult = newAddLivePotVo.getBetResult();
        if (CollectionUtils.isEmpty(betResult)) {
            return;
        }
        String userName = betResult.get(0).getUserName();
        Result<SysUserMoneyVo> moneyVoResult = userService.getMoneyByUserName(userName);
        if (moneyVoResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return;
        }
        SysUserMoneyVo sysUserMoneyVo = moneyVoResult.getDatas();
        GameRoomGroupUserVo vo = new GameRoomGroupUserVo();
        vo.setStatus(1);
        vo.setUserName(userName);
        vo.setMoney(sysUserMoneyVo.getMoney());
        vo.setGameId(newAddLivePotVo.getGameId());
        vo.setTableNum(newAddLivePotVo.getTableNum());
        syncTableNumGroup(vo);
    }

    @Override
    @Async
    public void syncTableNumGroup(GameRoomGroupUserVo vo) {
        String groupId = vo.getGameId() + "-" + vo.getTableNum();
        PushResult<GameRoomGroupUserVo> pushResult = PushResult.succeed(vo, SocketTypeConstant.TABLE_GROUP_USER, "桌台分组用户信息推送成功");
        //推送下注界面
        Result<String> push = pushService.sendMessageByGroupId(groupId, com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("下注界面桌台用户余额信息推送推送结果:groupId={},result={}", groupId, push);
        //推送游戏在线人数
        Integer status = vo.getStatus();
        Integer onlineNum = 0;
        if (status == 1) {
            return;
        } else if (status == 0) {
            onlineNum = -1;
        } else if (status == 2) {
            onlineNum = 1;
        }
        GameOnlineNumVo onlineNumVo = new GameOnlineNumVo();
        onlineNumVo.setGameId(vo.getGameId());
        onlineNumVo.setOnlineNum(onlineNum);
        //推送大厅
        PushResult<GameOnlineNumVo> pushResult1 = PushResult.succeed(onlineNumVo, SocketTypeConstant.GAME_ONLINE_NUM, "游戏在线人数推送成功");
        Result<String> onlineNumPush = pushService.sendAllMessage(com.alibaba.fastjson.JSONObject.toJSONString(pushResult1));
        log.info("大厅游戏在线人数推送结果,result={}", onlineNumPush);
    }

    @Override
    @Async
    public void syncPushGameStatus(GameList gameList) {
        //推送大厅
        PushResult<GameList> pushResult = PushResult.succeed(gameList, SocketTypeConstant.UPDATE_GAME_STATUS, "后台修改游戏状态信息推送成功");
        Result<String> hallPush = pushService.sendAllMessage(com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("大厅游戏配置信息推送结果,result={}", hallPush);
    }
}
