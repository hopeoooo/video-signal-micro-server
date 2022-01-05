package com.central.game.mapper;

import com.central.common.model.SuperPage;
import com.central.db.mapper.SuperMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.game.model.GameList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Mapper
public interface GameListMapper extends SuperMapper<GameList> {
    /**
     * 分页查询用户列表
     * @param page
     * @param superPage
     * @return
     */
    List<GameList> findList(Page<GameList> page, @Param("sp")SuperPage superPage);
}
