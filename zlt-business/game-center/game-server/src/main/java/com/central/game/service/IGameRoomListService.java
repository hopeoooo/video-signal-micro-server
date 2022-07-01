package com.central.game.service;

import com.central.common.service.ISuperService;
import com.central.game.model.GameRoomList;
import com.central.game.model.vo.GameRoomListVo;

import java.util.List;
import java.util.Map;

public interface IGameRoomListService  extends ISuperService<GameRoomList> {

    void updateRoomStatus(Map<String, Object> params);

    Boolean deleteBy(Long id);

    Boolean update(Long id,GameRoomList gameRoomList);

    List<GameRoomList> findGameRoomList(Long gameId);

    Boolean updateRoomStatus(Long id, Integer roomStatus,String maintainStart,String maintainEnd);

    GameRoomList findById(String id);

    List<GameRoomListVo> findRoomListByGameId(Long gameId,Long userId);

    GameRoomList findByGameIdAndGameRoomName(Long gameId, String gameRoomName);

    void setRoomStatus(GameRoomListVo vo);

    GameRoomListVo setTabelInfo(GameRoomListVo vo);
}
