package com.central.game.service;

import com.central.common.service.ISuperService;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.vo.GameRoomInfoOfflineVo;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
public interface IGameRoomInfoOfflineService extends ISuperService<GameRoomInfoOffline> {

    GameRoomInfoOffline findByGameIdAndTableNumAndBootNumAndBureauNum(String gameId, String tableNum, String bootNum, String bureauNum);

    GameRoomInfoOfflineVo getNewestTableInfoVo(Long gameId, String tableNum);

    GameRoomInfoOffline getNewestTableInfo(Long gameId, String tableNum);
}

