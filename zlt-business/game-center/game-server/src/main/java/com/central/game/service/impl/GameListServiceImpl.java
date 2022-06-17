package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.PageResult;
import com.central.game.mapper.GameListMapper;
import com.central.game.model.GameList;
import com.central.game.model.GameRoomList;
import com.central.game.service.IGameListService;
import com.central.game.service.IGameRoomGroupUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.central.common.model.SuperPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.service.impl.SuperServiceImpl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@Service
@CacheConfig(cacheNames = {"gameList"})
public class GameListServiceImpl extends SuperServiceImpl<GameListMapper, GameList> implements IGameListService {

    @Autowired
    private IGameRoomGroupUserService gameRoomGroupUserService;
    /**
     * 列表
     * @param superPage
     * @return
     */
    @Override
    public PageResult<GameList> findList(SuperPage superPage){
        Page<GameList> page = new Page<>(superPage.getPage(), superPage.getLimit());
        List<GameList> list  =  baseMapper.findList(page, superPage);
        return PageResult.<GameList>builder().data(list).count(page.getTotal()).build();
    }

    @Override
//    @Cacheable(key = "#p0")
    public GameList findById(Long gameId) {
        return baseMapper.selectById(gameId);
    }

    @Override
    public List<GameList> findAll() {
        List<GameList> gameLists = baseMapper.findEnableAllGame();
        //查询在线人数
        for (GameList game : gameLists) {
            Integer onlineNum = gameRoomGroupUserService.getGameOnlineNum(game.getId());
            game.setOnlineNum(onlineNum);
        }
        return gameLists;
    }
}
