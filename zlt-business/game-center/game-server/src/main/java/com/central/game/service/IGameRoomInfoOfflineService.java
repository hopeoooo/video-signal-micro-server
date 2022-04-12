package com.central.game.service;

import com.central.common.service.ISuperService;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.vo.GameRoomListVo;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
public interface IGameRoomInfoOfflineService extends ISuperService<GameRoomInfoOffline> {

    GameRoomInfoOffline findByGameIdAndTableNumAndBootNumAndBureauNum(String gameId, String tableNum, String bootNum, String bureauNum);

    GameRoomInfoOffline getNewestTableInfo(Long gameId, String tableNum);

    GameRoomListVo getNewestTableInfoVo(Long gameId, String tableNum);
}

