package com.central.game.service;

import com.central.common.model.PageResult;
import com.central.common.model.SuperPage;
import com.central.common.service.ISuperService;
import com.central.game.model.GameList;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
public interface IGameListService extends ISuperService<GameList> {

//    void updateGameStatus(Map<String, Object> params);
    /**
     * 列表
     * @param superPage
     * @return
     */
    PageResult<GameList> findList(SuperPage superPage);

    GameList findById(Long gameId);

    List<GameList> findAll();

    void syncPushGameStatus(GameList gameList);
}

