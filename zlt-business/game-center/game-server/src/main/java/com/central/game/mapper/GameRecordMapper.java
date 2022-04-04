package com.central.game.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.db.mapper.SuperMapper;
import com.central.game.dto.GameRecordDto;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.dto.HomeHistogramDto;
import com.central.game.dto.HomePageDto;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameRecordBetPageCo;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.vo.GameRecordVo;
import com.central.game.model.vo.GameRecordBackstageVo;
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


    List<GameRecordBackstageVo> findGameRecordList(Page<GameRecordBackstageVo> page, @Param("p") GameRecordBetCo params);

    GameRecordDto findGameRecordTotal( @Param("p") GameRecordBetCo params);

    GameRecordReportDto findBetAmountTotal(@Param("p") Map<String, Object> params);

    GameRecordReportDto findValidbetTotal(@Param("p") Map<String, Object> params);

    GameRecordReportDto findWinningAmountTotal(@Param("p") Map<String, Object> params);

    List<GameRecord> getGameRecordByParent(@Param("p") GameRecordBetCo params);

    HomePageDto findHomePageDto(@Param("parent")String parent);

    HomeHistogramDto findHomeHistogramDto(@Param("p") Map<String, Object> params);

    List<GameRecordVo> findBetList(Page<GameRecordVo> page, @Param("p") GameRecordBetPageCo params);
}
