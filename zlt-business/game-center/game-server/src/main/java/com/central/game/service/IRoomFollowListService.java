package com.central.game.service;

import com.central.common.service.ISuperService;
import com.central.game.model.RoomFollowList;
import com.central.game.model.vo.GameRoomListVo;

import java.util.List;

/**
 * 用户钱包表
 *
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
public interface IRoomFollowListService extends ISuperService<RoomFollowList> {

    List<GameRoomListVo> getRoomFollowList(Long userId);
}

