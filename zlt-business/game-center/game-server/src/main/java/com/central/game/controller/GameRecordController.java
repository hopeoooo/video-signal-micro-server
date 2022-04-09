package com.central.game.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.CodeEnum;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.utils.AddrUtil;
import com.central.game.dto.GameRecordDto;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.dto.HomeHistogramDto;
import com.central.game.dto.HomePageDto;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameRecordBetPageCo;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRecordCo;
import com.central.game.model.co.GameRecordLivePotCo;
import com.central.game.model.vo.GameRecordVo;
import com.central.game.model.vo.GameRecordBackstageVo;
import com.central.game.model.vo.GameWinningRateVo;
import com.central.game.model.vo.LivePotVo;
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


    @ResponseBody
    @ApiOperation(value = "总投注记录-总计-后台")
    @GetMapping("/findGameRecordTotal")
    public Result<GameRecordDto> findGameRecordTotal(@Valid @ModelAttribute GameRecordBetCo params) {
        GameRecordDto gameRecordTotal = gameRecordService.findGameRecordTotal(params);
        return Result.succeed(gameRecordTotal);
    }

    @ApiOperation(value = "根据父级查询下注记录")
    @GetMapping("/getGameRecordByParent")
    public Result<List<GameRecord>> getGameRecordByParent(@ModelAttribute GameRecordBetCo params) {
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

    @ApiOperation(value = "查询本局即时彩池数据")
    @GetMapping("/getLivePot")
    public Result<List<LivePotVo>> getLivePot(@ModelAttribute GameRecordLivePotCo co) {
        List<LivePotVo> list = gameRecordService.getLivePot(co.getGameId(), co.getTableNum(), co.getBootNum(), co.getBureauNum());
        return Result.succeed(list);
    }

    @ApiOperation(value = "查询本局筹码区注单数据")
    @GetMapping("/getChipArea")
    public Result<List<GameRecord>> getChipArea(@ModelAttribute GameRecordLivePotCo co) {
        List<GameRecord> list = gameRecordService.getGameRecordByBureauNum(co.getGameId(), co.getTableNum(), co.getBootNum(), co.getBureauNum());
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

}
