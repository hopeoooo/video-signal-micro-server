package com.central.game.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.SuperPage;
import com.central.db.mapper.SuperMapper;
import com.central.game.model.GameList;
import com.central.game.model.GamePlayConfig;
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
public interface GamePlayConfigMapper extends SuperMapper<GamePlayConfig> {
    /**
     * 分页查询
     * @param page
     * @param superPage
     * @return
     */
    List<GamePlayConfig> findList(Page<GamePlayConfig> page, @Param("sp")SuperPage superPage);
}
