package com.central.game.service;

import com.central.common.service.ISuperService;
import com.central.game.model.GameRoomGroupUser;

import java.util.List;

public interface IGameRoomGroupUserService extends ISuperService<GameRoomGroupUser> {

    List<Long> getNotFullGroup(Long gameId, String tableNum);

    GameRoomGroupUser checkExist(Long gameId, String tableNum,Long userId);
}
