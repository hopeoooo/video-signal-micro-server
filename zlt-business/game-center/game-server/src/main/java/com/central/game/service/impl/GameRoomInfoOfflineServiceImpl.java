package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.mapper.GameRoomInfoOfflineMapper;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.vo.GameRoomListVo;
import com.central.game.service.IGameRoomInfoOfflineService;
import com.central.game.service.IGameRoomListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private IGameRoomListService gameRoomListService;

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

    @Override
    public GameRoomInfoOffline getNewestTableInfo(Long gameId, String tableNum) {
        LambdaQueryWrapper<GameRoomInfoOffline> qw = Wrappers.lambdaQuery();
        qw.eq(GameRoomInfoOffline::getGameId, gameId)
                .eq(GameRoomInfoOffline::getTableNum, tableNum)
                .orderByDesc(GameRoomInfoOffline::getUpdateTime)
                .last("limit 1");
        GameRoomInfoOffline infoOffline = gameRoomInfoOfflineMapper.selectOne(qw);
        //?????????????????????
        if (infoOffline != null && infoOffline.getTimes() != null && infoOffline.getSecond() != null) {
            //????????????????????????????????????????????????
            long second = (System.currentTimeMillis() - infoOffline.getTimes()) / 1000;
            long differ = infoOffline.getSecond() - second;
            Long currentSecond = differ > 0 ? differ : 0;
            infoOffline.setCurrentSecond(currentSecond.intValue());
        }
        return infoOffline;
    }

    @Override
    public GameRoomListVo getNewestTableInfoVo(Long gameId, String tableNum) {
        GameRoomListVo vo = new GameRoomListVo();
        GameRoomInfoOffline newestTableInfo = getNewestTableInfo(gameId, tableNum);
        if (newestTableInfo == null) {
            return vo;
        }
        BeanUtils.copyProperties(newestTableInfo, vo);
        gameRoomListService.setTabelInfo(vo);
        return vo;
    }
}
