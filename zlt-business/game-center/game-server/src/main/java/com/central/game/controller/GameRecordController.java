package com.central.game.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.utils.AddrUtil;
import com.central.game.dto.GameRecordDto;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRecordBetTotalCo;
import com.central.game.model.co.GameRecordCo;
import com.central.game.service.IGameRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    public Result<GameRecordDto> findGameRecordTotal(@Valid @ModelAttribute GameRecordBetTotalCo params) {
        GameRecordDto gameRecordTotal = gameRecordService.findGameRecordTotal(params);
        return Result.succeed(gameRecordTotal);
    }


    @ApiOperation(value = "保存")
    @PostMapping("/save")
    public Result save(@Valid @RequestBody GameRecordCo co, @LoginUser SysUser user, HttpServletRequest request) {
        String ip = AddrUtil.getRemoteAddr(request);
        Result result = gameRecordService.saveRecord(co, user, ip);
        return result;
    }

    @ResponseBody
    @ApiOperation(value = "投注报表")
    @GetMapping("/findBetAmountTotal")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, dataType = "Long"),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "Long"),
    })
    public Result<GameRecordReportDto> findBetAmountTotal(@RequestParam Map<String, Object> params) {
        GameRecordReportDto gameRecordReportDto = gameRecordService.findBetAmountTotal(params);
        return Result.succeed(gameRecordReportDto);
    }

    @ResponseBody
    @ApiOperation(value = "有效投注报表")
    @GetMapping("/findValidbetTotal")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, dataType = "Long"),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "Long"),
    })
    public Result<GameRecordReportDto> findValidbetTotal(@RequestParam Map<String, Object> params) {
        GameRecordReportDto gameRecordReportDto = gameRecordService.findValidbetTotal(params);
        return Result.succeed(gameRecordReportDto);
    }

    @ResponseBody
    @ApiOperation(value = "派彩报表")
    @GetMapping("/findWinningAmountTotal")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, dataType = "Long"),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "Long"),
    })
    public Result<GameRecordReportDto> findWinningAmountTotal(@RequestParam Map<String, Object> params) {
        GameRecordReportDto gameRecordReportDto = gameRecordService.findWinningAmountTotal(params);
        return Result.succeed(gameRecordReportDto);
    }
}
