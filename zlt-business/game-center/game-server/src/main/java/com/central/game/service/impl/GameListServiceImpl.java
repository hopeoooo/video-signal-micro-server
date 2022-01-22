package com.central.game.service.impl;

import com.central.common.model.PageResult;
import com.central.game.mapper.GameListMapper;
import com.central.game.model.GameList;
import com.central.game.service.IGameListService;
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
public class GameListServiceImpl extends SuperServiceImpl<GameListMapper, GameList> implements IGameListService {
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
}
