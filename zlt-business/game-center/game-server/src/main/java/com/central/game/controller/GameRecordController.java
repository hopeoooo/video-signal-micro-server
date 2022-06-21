package com.central.game.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.utils.AddrUtil;
import com.central.game.dto.*;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRecord;
import com.central.game.model.co.*;
import com.central.game.model.vo.*;
import com.central.game.service.IGameLotteryResultService;
import com.central.game.service.IGameRecordService;
import com.central.user.model.vo.RankingListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@RestController
@RequestMapping("/gameRecord")
@Api(tags = "游戏下注记录")
public class GameRecordController {

    @Autowired
    private IGameRecordService gameRecordService;

    @Autowired
    private IGameLotteryResultService gameLotteryResultService;


    /**
     * 分页查询游戏下注数据
     *
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "查询游戏下注数据-后台")
    @GetMapping("/findList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer")
    })
    public Result<PageResult<GameRecordBackstageVo>> findList(@Valid @ModelAttribute GameRecordBetCo params) {
        PageResult<GameRecordBackstageVo> list = gameRecordService.findList(params);
        return Result.succeed(list);
    }

    @ApiOperation(value = "查询当前用户投注记录-前台")
    @GetMapping("/findBetList")
    public Result<PageResult<GameRecordVo>> findList(@Valid @ModelAttribute GameRecordBetPageCo params, @LoginUser SysUser user) {
        params.setUserId(user.getId());
        PageResult<GameRecordVo> list = gameRecordService.findBetList(params);
        return Result.succeed(list);
    }

    @ApiOperation(value = "查询本局投注明细记录-前台")
    @GetMapping("/findBureauBetDetail")
    public Result<List<GameRecord>> findBureauBetDetail(@Valid @ModelAttribute GameRecordDetailCo params, @LoginUser SysUser user) {
        List<GameRecord> list = gameRecordService.lambdaQuery().eq(GameRecord::getGameId, params.getGameId()).eq(GameRecord::getTableNum, params.getTableNum())
                .eq(GameRecord::getBootNum, params.getBootNum()).eq(GameRecord::getBureauNum, params.getBureauNum()).eq(GameRecord::getUserId, user.getId())
                .list();
        return Result.succeed(list);
    }

    @ResponseBody
    @ApiOperation(value = "总投注记录-总计-后台")
    @GetMapping("/findGameRecordTotal")
    public Result<GameRecordDto> findGameRecordTotal(@Valid @ModelAttribute GameRecordBetCo params) {
        GameRecordDto gameRecordTotal = gameRecordService.findGameRecordTotal(params);
        return Result.succeed(gameRecordTotal);
    }

    @ApiOperation(value = "根据父级查询下注记录")
    @GetMapping("/getGameRecordByParent")
    public Result<List<GameRecord>> getGameRecordByParent(@Valid @ModelAttribute GameRecordBetCo params) {
        List<GameRecord> list = gameRecordService.getGameRecordByParent(params);
        return Result.succeed(list);
    }


    @ApiOperation(value = "保存下注记录")
    @PostMapping("/save")
    public Result save(@Valid @RequestBody GameRecordCo co, @LoginUser SysUser user, HttpServletRequest request) {
        String ip = AddrUtil.getRemoteAddr(request);
        Result<List<LivePotVo>> result = gameRecordService.saveRecord(co, user, ip);
        return result;
    }

    @ApiOperation(value = "查询最新局即时彩池数据")
    @GetMapping("/getLivePot")
    public Result<NewAddLivePotVo> getLivePot(@Valid @ModelAttribute GameRecordLivePotCo co) {
        NewAddLivePotVo list = gameRecordService.getLivePot(co.getGameId(), co.getTableNum());
        return Result.succeed(list);
    }

    @ApiOperation(value = "查询最新局筹码区注单数据")
    @GetMapping("/getChipArea")
    public Result<List<GameRecord>> getChipArea(@Valid @ModelAttribute GameRecordLivePotCo co) {
        List<GameRecord> list = gameRecordService.getGameRecordByBureauNum(co.getGameId(), co.getTableNum());
        return Result.succeed(list);
    }

    @ResponseBody
    @ApiOperation(value = "投注报表")
    @GetMapping("/findBetAmountTotal")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false),
    })
    public Result<GameRecordReportDto> findBetAmountTotal(@RequestParam Map<String, Object> params) {
        GameRecordReportDto gameRecordReportDto = gameRecordService.findBetAmountTotal(params);
        return Result.succeed(gameRecordReportDto);
    }

    @ResponseBody
    @ApiOperation(value = "有效投注报表")
    @GetMapping("/findValidbetTotal")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false),
    })
    public Result<GameRecordReportDto> findValidbetTotal(@RequestParam Map<String, Object> params) {
        GameRecordReportDto gameRecordReportDto = gameRecordService.findValidbetTotal(params);
        return Result.succeed(gameRecordReportDto);
    }

    @ResponseBody
    @ApiOperation(value = "派彩报表")
    @GetMapping("/findWinningAmountTotal")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false),
    })
    public Result<GameRecordReportDto> findWinningAmountTotal(@RequestParam Map<String, Object> params) {
        GameRecordReportDto gameRecordReportDto = gameRecordService.findWinningAmountTotal(params);
        return Result.succeed(gameRecordReportDto);
    }

    @ResponseBody
    @ApiOperation(value = "查询首页报表")
    @GetMapping("/findHomePageDto")
    public Result<HomePageDto> findHomePageDto(@RequestParam(value = "parent", required = false)String parent) {
        HomePageDto homePageDto = gameRecordService.findHomePageDto(parent);
        return Result.succeed(homePageDto);
    }

    @ResponseBody
    @ApiOperation(value = "会员游戏报表")
    @GetMapping("/findUserGameReportDto")
    public Result<List<UserGameReportDto>> findUserGameReportDto(@RequestParam(value = "userId")Long userId){
        List<UserGameReportDto> userGameReportDto = gameRecordService.findUserGameReportDto(userId);
        return Result.succeed(userGameReportDto);
    }

    @ResponseBody
    @ApiOperation(value = "admin首页柱状图查询")
    @GetMapping("/findHomeHistogramDto")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false),
    })
    public Result<HomeHistogramDto> findHomeHistogramDto(@RequestParam Map<String, Object> params) {
        HomeHistogramDto homeHistogramDto = gameRecordService.findHomeHistogramDto(params);
        return Result.succeed(homeHistogramDto);
    }

    @ApiOperation(value = "今日排行-赢家榜")
    @GetMapping("/getRichList")
    public Result<List<RankingListVo>> getTodayLotteryList() {
        List<RankingListVo> list = gameRecordService.getTodayLotteryList();
        return Result.succeed(list);
    }

    @ApiOperation(value = "今日排行-投注榜")
    @GetMapping("/getBetList")
    public Result<List<RankingListVo>> getTodayBetList() {
        List<RankingListVo> list = gameRecordService.getTodayBetList();
        return Result.succeed(list);
    }

    @ApiOperation(value = "个人资讯-今日有效投注")
    @GetMapping("/getTodayValidbet")
    public Result<String> getTodayValidbet(@LoginUser SysUser user) {
        String todayValidbet = gameRecordService.getTodayValidbet(user.getId());
        return Result.succeed(todayValidbet,"查询成功");
    }

    @ApiOperation(value = "个人资讯-累计有效投注")
    @GetMapping("/getTotalValidbet")
    public Result<String> getTotalValidbet(@LoginUser SysUser user) {
        String todayValidbet = gameRecordService.getTotalValidbet(user.getId());
        return Result.succeed(todayValidbet,"查询成功");
    }

    @ApiOperation(value = "个人资讯-游戏胜率")
    @GetMapping("/getGameWinningRate")
    public Result<List<GameWinningRateVo>> getGameWinningRate(@LoginUser SysUser user) {
        List<GameWinningRateVo> gameWinningRate = gameRecordService.getGameWinningRate(user.getId());
        return Result.succeed(gameWinningRate);
    }

    @ApiOperation(value = "根据游戏ID查询登录用户最新投注记录")
    @GetMapping("/getNewestBetListByGameId/{gameId}")
    public Result<List<GameRecord>> getNewestBetListByGameId(@PathVariable Long gameId,@LoginUser SysUser user) {
        List<GameRecord> gameRecordList = gameRecordService.getNewestBetListByGameId(gameId, user.getId());
        return Result.succeed(gameRecordList);
    }

    @ApiOperation(value = "根据userId清空所有投注记录")
    @GetMapping("/clearGuestGameRecord/{userId}")
    public Result clearGuestGameRecord(@PathVariable Long userId) {
        gameRecordService.clearGuestGameRecord(userId);
        return Result.succeed();
    }

    @ResponseBody
    @ApiOperation(value = "会员报表")
    @GetMapping("/findUserReportDto")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false),
    })
    public Result<List<UserReportDto>> findUserReportDto(@RequestParam Map<String, Object> params) {
        List<UserReportDto> userReportDtos = gameRecordService.findUserReportDto(params);
        return Result.succeed(userReportDtos);
    }


    @ApiOperation(value = "投注金额排行")
    @GetMapping("/findValidBetRankingList")
    public Result<List<RankingBackstageVo>> findValidBetRankingList(@RequestParam(value ="listId", required = false) List<Long> listId) {
        List<RankingBackstageVo> validBetRankingList = gameRecordService.findValidBetRankingList(listId);
        return Result.succeed(validBetRankingList);
    }



    @ApiOperation(value = "盈利金额排行")
    @GetMapping("/findWinLossRankingList")
    public Result<List<RankingBackstageVo>> findWinLossRankingList() {
        List<RankingBackstageVo> validBetRankingList = gameRecordService.findWinLossRankingList();
        return Result.succeed(validBetRankingList);
    }

    @ApiOperation(value = "计算开奖结果测试")
    @GetMapping("/gameLotteryResult/{id}")
    public Result gameLotteryResult(@PathVariable Long id) {
        GameLotteryResult lotteryResult = gameLotteryResultService.getById(id);
        gameLotteryResultService.calculateBetResult(lotteryResult);
        return Result.succeed();
    }

}
