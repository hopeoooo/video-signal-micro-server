package com.central.game.service;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.service.ISuperService;
import com.central.game.dto.*;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameRecordBetPageCo;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRecordCo;
import com.central.game.model.vo.*;
import com.central.user.model.vo.RankingListVo;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
public interface IGameRecordService extends ISuperService<GameRecord> {

    PageResult<GameRecordBackstageVo> findList(GameRecordBetCo map);

    GameRecordDto findGameRecordTotal(GameRecordBetCo params);

    Result<List<LivePotVo>> saveRecord(GameRecordCo co, SysUser user, String ip);

    GameRecordReportDto findBetAmountTotal(Map<String, Object> params);

    GameRecordReportDto findValidbetTotal(Map<String, Object> params);

    GameRecordReportDto findWinningAmountTotal(Map<String, Object> params);

    List<GameRecord> getGameRecordByParent(GameRecordBetCo params);

    HomePageDto findHomePageDto(String parent);

    List<LivePotVo> getBureauNumLivePot(Long gameId, String tableNum, String bootNum, String bureauNum);

    NewAddLivePotVo getLivePot(Long gameId, String tableNum);

    HomeHistogramDto findHomeHistogramDto(Map<String, Object> params);

    PageResult<GameRecordVo> findBetList(GameRecordBetPageCo params);

    List<GameRecord> getGameRecordByBureauNum(Long gameId, String tableNum);

    List<PayoutResultVo> getPayoutResult(Long gameId, String tableNum, String bootNum, String bureauNum);

    List<RankingListVo> getTodayLotteryList();

    List<RankingListVo> getTodayBetList();

    String getTodayValidbet(Long userId);

    String getTotalValidbet(Long userId);

    List<GameWinningRateVo> getGameWinningRate(Long userId);

    List<GameRecord> getNewestBetListByGameId(Long gameId, Long userId);

    void calculateWashCode(GameRecord record);

    void calculateFlowCode(GameRecord record);

    void syncDeleteGuestRecordBureauNum(Long gameId, String tableNum, String bootNum, String bureauNum);

    List<UserReportDto> findUserReportDto(Map<String, Object> params);

    List<RankingBackstageVo>  findValidBetRankingList(List<Long> listId);

    List<RankingBackstageVo>  findWinLossRankingList();

    List<UserGameReportDto> findUserGameReportDto(Long userId);

    void clearGuestGameRecord(Long userId);
}

