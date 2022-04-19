package com.central.game.service;

import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.GameRoomList;
import com.central.game.model.vo.LivePotVo;
import com.central.game.model.vo.NewAddLivePotVo;

import java.util.List;

public interface IPushGameDataToClientService {

    void syncLivePot(NewAddLivePotVo newAddLivePotVo);

    void syncPushPayoutResult(GameLotteryResult result);

    void pushLotterResult(GameLotteryResult result);

    void syncPushGameRoomInfo(GameRoomInfoOffline po);

    void syncPushGameRoomStatus(GameRoomList po);

    void syncTableNumGroup(NewAddLivePotVo newAddLivePotVo);
}
