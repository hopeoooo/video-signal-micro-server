package com.central.game.service;

import com.central.common.service.ISuperService;
import com.central.game.model.GameRoomList;

import java.util.List;

public interface IGameRoomListService  extends ISuperService<GameRoomList> {

    List<GameRoomList> findGameRoomList(Long gameId);

    Boolean updateRoomStatus(Long id, Integer roomStatus,String maintainStart,String maintainEnd);
}
