package com.central.game.service;

import com.central.common.model.PageResult2;
import com.central.common.model.SuperPage;
import com.central.common.service.ISuperService;
import com.central.game.model.GameList;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
public interface IGameListService extends ISuperService<GameList> {
    /**
     * 列表
     * @param superPage
     * @return
     */
    PageResult2<GameList> findList(SuperPage superPage);
}

