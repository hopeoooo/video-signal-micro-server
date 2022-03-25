package com.central.game.mapper;

import com.central.db.mapper.SuperMapper;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.co.GameLotteryResultCo;
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
public interface GameLotteryResultMapper extends SuperMapper<GameLotteryResult> {

    List<GameLotteryResult> getLotteryResultList(@Param("p") GameLotteryResultCo co);
}
