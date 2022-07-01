package com.central.game.mapper;

import com.central.db.mapper.SuperMapper;
import com.central.game.model.GameRoomList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface GameRoomListMapper extends SuperMapper<GameRoomList> {

    void updateRoomStatus(@Param("p") Map<String, Object> params);
}
