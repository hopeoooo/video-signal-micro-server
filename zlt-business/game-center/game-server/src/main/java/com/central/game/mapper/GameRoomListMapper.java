package com.central.game.mapper;

import com.central.db.mapper.SuperMapper;
import com.central.game.model.GameRoomList;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameRoomListMapper extends SuperMapper<GameRoomList> {
}
