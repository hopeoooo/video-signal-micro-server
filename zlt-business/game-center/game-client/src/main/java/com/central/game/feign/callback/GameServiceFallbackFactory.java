package com.central.game.feign.callback;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.game.dto.*;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRecord;
import com.central.game.model.co.*;
import com.central.game.model.co.GameListCo;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRoomListCo;
import com.central.game.feign.GameService;
import com.central.game.model.GameList;
import com.central.game.model.GameRoomList;
import com.central.game.model.vo.GameRecordBackstageVo;
import com.central.game.model.vo.GameRoomGroupUserVo;
import com.central.game.model.vo.RankingBackstageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 降级工场
 */
@Slf4j
public class GameServiceFallbackFactory implements FallbackFactory<GameService> {

    @Override
    public GameService create(Throwable throwable) {
        return new GameService() {
            @Override
            public Result save(GameListCo gameList) {
                log.error("游戏保存失败:{}", gameList, throwable);
                return Result.failed("游戏保存失败");
            }

            @Override
            public Result<List<GameList>> findAll() {
                log.error("查询全部游戏失败", throwable);
                return Result.failed("查询全部游戏失败");
            }

            @Override
            public Result<GameList> findById(Long id) {
                log.error("根据ID查询游戏详情失败", throwable);
                return Result.failed("根据ID查询游戏详情失败");
            }

            @Override
            public Result deleteById(Long id) {
                log.error("根据ID删除游戏失败", throwable);
                return Result.failed("根据ID删除游戏失败");
            }

            @Override
            public Result<List<GameList>> findAllOpenRate() {
                log.error("查询全部开启返水的游戏失败", throwable);
                return Result.failed("查询全部开启返水的游戏失败");
            }

            @Override
            public Result<List<GameList>> findGameList(Integer state) {
                log.error("findGameList查询洗码配置列表失败", throwable);
                return Result.failed("查询失败");
            }

            @Override
            public Result<List<GameList>> findEnableGameListByGameId(Long gameId,Integer state) {
                log.error("findEnableGameListByGameId查询洗码配置列表失败", throwable);
                return Result.failed("查询失败");
            }

            @Override
            public Result updateGameStatus(Map<String, Object> params) {
                log.error("修改游戏状态失败", throwable);
                return Result.failed("修改游戏状态失败");
            }

            @Override
            public Result updateRoomStatus(Map<String, Object> params) {
                log.error("修改房间状态失败", throwable);
                return Result.failed("修改房间状态失败");
            }

            @Override
            public Result<List<GameRoomList>> findList(Long gameId) {
                log.error("findList查询房间列表失败", throwable);
                return Result.failed("查询失败");
            }

            @Override
            public Result save(GameRoomListCo gameRoomList) {
                log.error("save房间失败", throwable);
                return Result.failed("保存房间失败");
            }

            @Override
            public Result updateRoomStatus(Long id, Integer roomStatus, String maintainStart, String maintainEnd) {
                log.error("更新房间失败", throwable);
                return Result.failed("更新房间失败");
            }
            @Override
            public Result roomDeleteById(Long id) {
                log.error("删除房间失败", throwable);
                return Result.failed("删除房间失败");
            }

            @Override
            public Result<GameRoomList> findRoomDetailById(Long id) {
                log.error("findRoomDetailById查询房间详情失败id:{}", id);
                return Result.failed("查询房间详情失败");
            }

            @Override
            public Result<List<GameRoomList>> findRoomDetailByIds(String ids) {
                log.error("findRoomDetailByIds查询房间详情失败ids:{}", ids);
                return Result.failed("查询房间详情失败");
            }

            @Override
            public Result<PageResult<GameRecordBackstageVo>> findList(GameRecordBetCo params) {
                log.error("findList查询下注记录失败:{}", params);
                return Result.failed("查询下注记录失败");
            }

            @Override
            public Result<List<GameRecord>> getGameRecordByParent(GameRecordBetCo params) {
                log.error("getGameRecordByParent查询下注记录失败:{}", params);
                return Result.failed("查询下注记录失败");
            }

            @Override
            public Result<List<GameLotteryResult>> getLotteryResultList(GameLotteryResultCo params) {
                log.error("getLotteryResultList 查询开奖结果记录失败:{}", params);
                return Result.failed("查询开奖结果失败");
            }

            @Override
            public Result<PageResult<GameLotteryResult>> findList(GameLotteryResultBackstageCo params) {
                log.error("findList查询开奖记录失败:{}", params);
                return Result.failed("查询开奖记录失败");
            }

            @Override
            public Result<GameRecordDto> findGameRecordTotal(GameRecordBetCo params) {
                log.error("findGameRecordTotal查询下注记录总计失败:{}", params);
                return Result.failed("查询下注记录总计失败");
            }

            @Override
            public Result<GameRecordReportDto> findBetAmountTotal(Map<String, Object> params) {
                log.error("findBetAmountTotal查询投注报表失败:{}", params);
                return Result.failed("查询投注报表失败");
            }

            @Override
            public Result<GameRecordReportDto> findValidbetTotal(Map<String, Object> params) {
                log.error("findValidbetTotal查询有效投注报表失败:{}", params);
                return Result.failed("查询有效投注报表失败");
            }

            @Override
            public Result<GameRecordReportDto> findWinningAmountTotal(Map<String, Object> params) {
                log.error("findWinningAmountTotal查询派彩报表失败:{}", params);
                return Result.failed("查询派彩报表失败");
            }

            @Override
            public Result<HomePageDto> findHomePageDto(String parent) {
                log.error("findHomePageDto查询首页报表失败:{}", parent);
                return Result.failed("查询首页报表失败");
            }

            @Override
            public Result<HomeHistogramDto> findHomeHistogramDto(Map<String, Object> params) {
                log.error("findHomePageDto查询首页柱状图失败:{}", params);
                return Result.failed("查询首页柱状图失败");
            }

            @Override
            public Result<List<UserReportDto>> findUserReportDto(Map<String, Object> params) {
                log.error("findUserReportDto会员报表失败:{}", params);
                return Result.failed("会员报表");
            }

            @Override
            public Result<List<RankingBackstageVo>> findValidBetRankingList(List<Long> listId) {
                log.error("findValidBetRankingList查询投注金额排行失败");
                return Result.failed("查询投注金额排行失败");
            }

            @Override
            public Result<List<RankingBackstageVo>> findWinLossRankingList() {
                log.error("findWinLossRankingList查询盈利金额排行失败");
                return Result.failed("查询盈利金额排行失败");
            }

            @Override
            public Result<List<UserGameReportDto>> findUserGameReportDto(Long userId) {
                log.error("findUserGameReportDto会员游戏报表失败:{}", userId);
                return Result.failed("会员游戏报表失败");
            }

            @Override
            public Result removeTableNumGroup(String userName) {
                log.error("removeTableNumGroup删除桌台用户失败，userName:{}",userName);
                return Result.failed("删除桌台用户失败");
            }

            @Override
            public Result<List<GameRoomGroupUserVo>> getAllGroupListByUserName(String userName) {
                log.error("getAllGroupListByUserName查询用户虚拟分组失败，userName:{}",userName);
                return Result.failed("查询用户虚拟分组失败");
            }

            @Override
            public Result clearGuestGameRecord(Long userId) {
                log.error("clearGuestGameRecord根据userId清空投注记录失败，userId:{}",userId);
                return Result.failed("查询失败");
            }

            @Override
            public Result<GameRoomList> findByGameIdAndGameRoomName(Long gameId, String gameRoomName) {
                log.error("findByGameIdAndGameRoomName查询失败，gameId:{},gameRoomName:{}",gameId,gameRoomName);
                return Result.failed("查询失败");
            }

            @Override
            public Result clearGuestFollowList(Long userId) {
                log.error("clearGuestFollowList清空关注列表失败，userId:{}",userId);
                return Result.failed("清空关注列表失败");
            }
        };
    }
}
