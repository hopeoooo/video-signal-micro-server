package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.mapper.GameRoomListMapper;
import com.central.game.model.GameRoomList;
import com.central.game.service.IGameRoomListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GameRoomListServiceImpl extends SuperServiceImpl<GameRoomListMapper, GameRoomList> implements IGameRoomListService {

    @Autowired
    private GameRoomListMapper gameRoomListMapper;

    @Override
    public List<GameRoomList> findGameRoomList(Long gameId) {
        LambdaQueryWrapper<GameRoomList> lqw = Wrappers.lambdaQuery();
        if(gameId != null){
            lqw.eq(GameRoomList::getGameId, gameId);
        }
        lqw.orderByAsc(GameRoomList::getId);
        List<GameRoomList> gameRoomLists = gameRoomListMapper.selectList(lqw);
        return gameRoomLists;
    }

    @Override
    public Boolean updateRoomStatus(Long id, Integer roomStatus) {
        GameRoomList gameRoomList = gameRoomListMapper.selectById(id);
        if(gameRoomList == null){
            return false;
        }
        gameRoomList.setRoomStatus(roomStatus);
        gameRoomListMapper.updateById(gameRoomList);
        return true;
    }
}
