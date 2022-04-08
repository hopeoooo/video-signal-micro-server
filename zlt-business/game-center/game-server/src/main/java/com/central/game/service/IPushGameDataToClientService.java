package com.central.game.service;

import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.vo.LivePotVo;

import java.util.List;

public interface IPushGameDataToClientService {

    void syncLivePot(Long gameId, String tableNum, String bootNum, String bureauNum, List<LivePotVo> list);

    void syncPushPayoutResult(GameLotteryResult result);

    void pushLotterResult(GameLotteryResult result);

    void syncPushGameRoomInfo(GameRoomInfoOffline po);
}
