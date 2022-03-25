package com.central.game.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.db.mapper.SuperMapper;
import com.central.game.dto.GameRecordDto;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRecordBetTotalCo;
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
public interface GameLotteryResultMapper extends SuperMapper<GameLotteryResult> {

    List<GameLotteryResult> getLotteryResultList(@Param("p") GameLotteryResultCo co);
}
