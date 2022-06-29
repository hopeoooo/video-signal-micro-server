package com.central.game.mapper;

import com.central.db.mapper.SuperMapper;
import com.central.game.model.GameRoomList;
import com.central.game.model.RoomFollowList;
import com.central.game.model.vo.GameRoomListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户钱包表
 * 
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
@Mapper
public interface RoomFollowListMapper extends SuperMapper<RoomFollowList> {

    List<GameRoomListVo> getRoomFollowList(@Param("userId") Long userId);
}
