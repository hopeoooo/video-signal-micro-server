package com.central.game.mapper;

import com.central.db.mapper.SuperMapper;
import com.central.game.model.GameRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Mapper
public interface GameRecordMapper extends SuperMapper<GameRecord> {
}
