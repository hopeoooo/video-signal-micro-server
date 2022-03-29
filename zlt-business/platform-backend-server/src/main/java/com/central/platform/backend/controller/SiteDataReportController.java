package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.feign.GameService;
import com.central.platform.backend.util.DateUtil;
import com.central.platform.backend.model.dto.SiteDataReportDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 在线会员报表
 */
@Slf4j
@RestController
@Api(tags = "站点数据报表")
@RequestMapping("/site/data")
public class SiteDataReportController {

    @Resource
    private GameService gameService;

    @ApiOperation(value = "会员报表查询")
    @GetMapping("/find")
    public Result<Map<String,SiteDataReportDto>> find(){
        Map<String,SiteDataReportDto> map = new HashMap<>();
        map.put("today",getSiteDataReportDto(DateUtil.getToday()));
        map.put("yesterday",getSiteDataReportDto(DateUtil.getYesterday()));
        map.put("week",getSiteDataReportDto(DateUtil.getWeek()));
        map.put("lastWeek",getSiteDataReportDto(DateUtil.getLastWeek()));
        map.put("month",getSiteDataReportDto(DateUtil.getMonth()));
        map.put("lastMonth",getSiteDataReportDto(DateUtil.getLastMonth()));
        map.put("nearlyTwoMonths",getSiteDataReportDto(DateUtil.getNearlyTwoMonths()));
        return Result.succeed(map);
    }

    private SiteDataReportDto getSiteDataReportDto(Map<String, Object> map ){
        SiteDataReportDto siteDataReportDto = new SiteDataReportDto();
        Result<GameRecordReportDto> betAmountTotal = gameService.findBetAmountTotal(map);
        Result<GameRecordReportDto> validbetTotal = gameService.findValidbetTotal(map);
        Result<GameRecordReportDto> winningAmountTotal = gameService.findWinningAmountTotal(map);
        siteDataReportDto.setBetAmount(betAmountTotal.getDatas().getAmount());
        siteDataReportDto.setBetAmountNum(betAmountTotal.getDatas().getNum());
        siteDataReportDto.setValidbet(validbetTotal.getDatas().getAmount());
        siteDataReportDto.setValidbetNum(validbetTotal.getDatas().getNum());
        siteDataReportDto.setWinningAmount(winningAmountTotal.getDatas().getAmount());
        siteDataReportDto.setWinningAmountNum(winningAmountTotal.getDatas().getNum());
        return siteDataReportDto;
    }
}
