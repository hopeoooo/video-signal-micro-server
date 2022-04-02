package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.mapper.GameRoomInfoOfflineMapper;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.GameRoomList;
import com.central.game.service.IGameRoomInfoOfflineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@Service
public class GameRoomInfoOfflineServiceImpl extends SuperServiceImpl<GameRoomInfoOfflineMapper, GameRoomInfoOffline> implements IGameRoomInfoOfflineService {

    @Autowired
    private GameRoomInfoOfflineMapper gameRoomInfoOfflineMapper;

    @Override
    public GameRoomInfoOffline findByGameIdAndTableNumAndBootNumAndBureauNum(String gameId, String tableNum, String bootNum, String bureauNum) {
        LambdaQueryWrapper<GameRoomInfoOffline> qw = Wrappers.lambdaQuery();
        qw.eq(GameRoomInfoOffline::getGameId, gameId)
                .eq(GameRoomInfoOffline::getTableNum, tableNum)
                .eq(GameRoomInfoOffline::getBootNum, bootNum)
                .eq(GameRoomInfoOffline::getBureauNum, bureauNum);
        GameRoomInfoOffline infoOffline = gameRoomInfoOfflineMapper.selectOne(qw);
        return infoOffline;
    }
}
