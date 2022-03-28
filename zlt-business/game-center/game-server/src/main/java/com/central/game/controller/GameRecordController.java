package com.central.game.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.CodeEnum;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.utils.AddrUtil;
import com.central.game.constants.GameListEnum;
import com.central.game.dto.GameRecordDto;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.dto.HomePageDto;
import com.central.game.model.GameList;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRecordCo;
import com.central.game.model.vo.LivePotVo;
import com.central.game.service.IGameRecordService;
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
    @ApiOperation(value = "查询游戏下注数据")
    @GetMapping("/findList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer")
    })
    public Result<PageResult<GameRecord>> findList(@Valid @ModelAttribute GameRecordBetCo params) {
        PageResult<GameRecord> list = gameRecordService.findList(params);
        return Result.succeed(list);
    }


    @ResponseBody
    @ApiOperation(value = "总投注记录-总计")
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
        if (result.getResp_code() == CodeEnum.SUCCESS.getCode()) {
            //异步推送本局下注汇总数据（按玩法汇总）
            gameRecordService.syncLivePot(GameListEnum.BACCARAT.getGameId(), co.getTableNum(), co.getBootNum(), co.getBureauNum(), result.getDatas());
        }
        return result;
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
    @ApiImplicitParams({
        @ApiImplicitParam(name = "parent", value = "父级", required = false),
    })
    public Result<HomePageDto> findHomePageDto(@PathVariable("parent") String parent) {
        HomePageDto homePageDto = gameRecordService.findHomePageDto(parent);
        return Result.succeed(homePageDto);
    }
}
