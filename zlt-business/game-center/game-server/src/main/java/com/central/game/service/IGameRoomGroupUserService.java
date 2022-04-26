package com.central.game.service;

import com.central.common.model.SysUser;
import com.central.common.service.ISuperService;
import com.central.game.model.GameRoomGroupUser;
import com.central.game.model.vo.GameRoomGroupUserVo;

import java.util.List;

public interface IGameRoomGroupUserService extends ISuperService<GameRoomGroupUser> {

    List<Long> getNotFullGroup(Long gameId, String tableNum);

    GameRoomGroupUser checkExist(Long gameId, String tableNum, Long userId);

    void addGroup(Long gameId, String tableNum, SysUser sysUser);

    List<GameRoomGroupUserVo> getTableNumGroupList(Long gameId, String tableNum, SysUser sysUser);

    List<GameRoomGroupUserVo> getGroupList(String userName);

    void removeGroup(Long gameId, String tableNum, SysUser sysUser);

    void removeAllGroup(String userName);
}
