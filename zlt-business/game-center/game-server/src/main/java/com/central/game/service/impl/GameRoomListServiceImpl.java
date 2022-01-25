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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public Boolean updateRoomStatus(Long id, Integer roomStatus,String maintainStart,String maintainEnd) {
        GameRoomList gameRoomList = gameRoomListMapper.selectById(id);
        if(gameRoomList == null){
            return false;
        }
        Date maintainStartTemp=null;
        Date maintainEndTemp=null;
        gameRoomList.setRoomStatus(roomStatus);
        if (roomStatus==2) {
            if (maintainStart == null || maintainEnd == null) {
                return false;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                maintainStartTemp = sdf.parse(maintainStart);
                maintainEndTemp = sdf.parse(maintainEnd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        gameRoomList.setMaintainStart(maintainStartTemp);
        gameRoomList.setMaintainEnd(maintainEndTemp);
        gameRoomListMapper.updateById(gameRoomList);
        return true;
    }
}
