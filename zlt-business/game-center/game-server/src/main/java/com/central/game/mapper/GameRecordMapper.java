package com.central.game.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.dto.LoginLogPageDto;
import com.central.common.model.SuperPage;
import com.central.db.mapper.SuperMapper;
import com.central.game.dto.GameRecordDto;
import com.central.game.model.GameList;
import com.central.game.model.GameRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Mapper
public interface GameRecordMapper extends SuperMapper<GameRecord> {


    List<GameRecord> findGameRecordList(Page<GameRecord> page, @Param("p") Map<String, Object> params);
    GameRecordDto findGameRecordTotal( @Param("p") Map<String, Object> params);
}
