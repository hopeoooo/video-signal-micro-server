package com.central.game.mapper;

import com.central.db.mapper.SuperMapper;
import com.central.game.model.GameRoomGroupUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Mapper
public interface GameRoomGroupUserMapper extends SuperMapper<GameRoomGroupUser> {

    List<Long> getNotFullGroup(@Param("gameId") Long gameId, @Param("tableNum") String tableNum);

    GameRoomGroupUser checkExist(@Param("gameId") Long gameId, @Param("tableNum") String tableNum,@Param("userId") Long userId);
}
