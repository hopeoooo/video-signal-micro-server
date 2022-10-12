package com.central.game.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.db.mapper.SuperMapper;
import com.central.game.dto.*;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameRecordBetPageCo;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.vo.*;
import com.central.user.model.vo.RankingListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
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

    GameRecordVo findTotalBetList(@Param("p") GameRecordBetPageCo params);

    List<PayoutResultVo> getPayoutResult(@Param("gameId") Long gameId, @Param("tableNum") String tableNum, @Param("bootNum") String bootNum, @Param("bureauNum") String bureauNum);

    List<RankingListVo> getTodayLotteryList(@Param("startTime") String startTime,@Param("endTime") String endTime);

    List<RankingListVo> getTodayBetList(@Param("startTime") String startTime,@Param("endTime") String endTime);

    BigDecimal getTodayValidbet(@Param("userId") Long userId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    BigDecimal getTotalValidbet(@Param("userId") Long userId);

    List<BigDecimal> getGameWinningRate(@Param("userId") Long userId,@Param("gameId") Long gameId);

    List<LivePotVo> getLivePot(@Param("gameId") Long gameId, @Param("tableNum") String tableNum, @Param("bootNum") String bootNum, @Param("bureauNum") String bureauNum);

    void deleteGuestRecordBureauNum(@Param("userType") String userType, @Param("gameId") Long gameId, @Param("tableNum") String tableNum, @Param("bootNum") String bootNum, @Param("bureauNum") String bureauNum);

    List<UserReportDto> findUserReportDto(@Param("p") Map<String, Object> params);

    List<RankingBackstageVo>  findValidBetRankingList(@Param("listId")List<Long> listId);

    List<RankingBackstageVo>  findWinLossRankingList();

    List<UserGameReportDto> findUserGameReportDto(@Param("userId") Long userId);

    void clearGuestGameRecord(@Param("userId") Long userId);

}
