package com.central.game.service.impl;

import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.mapper.GameRoomGroupUserMapper;
import com.central.game.model.GameRoomGroupUser;
import com.central.game.service.IGameRoomGroupUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GameRoomGroupUserServiceImpl extends SuperServiceImpl<GameRoomGroupUserMapper, GameRoomGroupUser> implements IGameRoomGroupUserService {

    @Autowired
    private GameRoomGroupUserMapper gameRoomGroupUserMapper;

    @Override
    public List<Long> getNotFullGroup(Long gameId, String tableNum) {
        return gameRoomGroupUserMapper.getNotFullGroup(gameId,tableNum);
    }

    @Override
    public GameRoomGroupUser checkExist(Long gameId, String tableNum,Long userId) {
        return gameRoomGroupUserMapper.checkExist(gameId,tableNum,userId);
    }
}
