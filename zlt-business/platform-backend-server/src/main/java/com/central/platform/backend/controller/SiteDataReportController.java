package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.feign.GameService;
import com.central.platform.backend.model.dto.SiteDataReportDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public final static String start = " 00:00:00";

    public final static String end = " 23:59:59";

    public final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @ApiOperation(value = "会员报表查询")
    @GetMapping("/find")
    public Result<Map<String,SiteDataReportDto>> find(){
        Map<String,SiteDataReportDto> map = new HashMap<>();
        map.put("today",getSiteDataReportDto(getToday()));
        map.put("yesterday",getSiteDataReportDto(getYesterday()));
        map.put("week",getSiteDataReportDto(getWeek()));
        map.put("lastWeek",getSiteDataReportDto(getLastWeek()));
        map.put("month",getSiteDataReportDto(getMonth()));
        map.put("lastMonth",getSiteDataReportDto(getLastMonth()));
        map.put("nearlyTwoMonths",getSiteDataReportDto(getNearlyTwoMonths()));
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
    private Map<String, Object> getToday(){
        Calendar nowTime = Calendar.getInstance();
        String today = simpleDateFormat.format(nowTime.getTime());
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",today+start);
        map.put("endTime",today+end);
        return map;
    }

    private Map<String, Object> getYesterday(){
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.DATE, -1);
        String today = simpleDateFormat.format(nowTime.getTime());
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",today+start);
        map.put("endTime",today+end);
        return map;
    }

    private Map<String, Object> getWeek(){
        Date monday = getWeekStartDate();
        String mondayStr = simpleDateFormat.format(monday);

        Date sunday = getWeekEndDate();
        String sundayStr = simpleDateFormat.format(sunday);
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",mondayStr+start);
        map.put("endTime",sundayStr+end);
        return map;
    }

    private Map<String, Object> getLastWeek(){
        Date lastMonday = getLastWeekStartDate();
        String mondayStr = simpleDateFormat.format(lastMonday);

        Date lastSunday = getLastWeekEndDate();
        String sundayStr = simpleDateFormat.format(lastSunday);
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",mondayStr+start);
        map.put("endTime",sundayStr+end);
        return map;
    }

    private Map<String, Object> getMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        String startMonth = simpleDateFormat.format(calendar.getTime());

        calendar.add(Calendar.MONTH,1);//月增加1月
        calendar.add(Calendar.DAY_OF_MONTH,-1);//日期倒数一日,既得到本月最后一天
        String endMonth = simpleDateFormat.format(calendar.getTime());
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",startMonth+start);
        map.put("endTime",endMonth+end);
        return map;
    }

    private Map<String, Object> getLastMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,-1);
        String startMonth = simpleDateFormat.format(calendar.getTime());

        calendar.add(Calendar.MONTH,1);//月增加1月
        calendar.add(Calendar.DAY_OF_MONTH,-1);//日期倒数一日,既得到上月最后一天
        String endMonth = simpleDateFormat.format(calendar.getTime());
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",startMonth+start);
        map.put("endTime",endMonth+end);
        return map;
    }

    private Map<String, Object> getNearlyTwoMonths(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,-1);
        String startMonth = simpleDateFormat.format(calendar.getTime());

        calendar.add(Calendar.MONTH,2);//月增加2月
        calendar.add(Calendar.DAY_OF_MONTH,-1);//日期倒数一日,既得到本月最后一天
        String endMonth = simpleDateFormat.format(calendar.getTime());
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",startMonth+start);
        map.put("endTime",endMonth+end);
        return map;
    }
    // 获得本周第一天，周日
    public static Date getWeekStartDate() {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 1);
        return cal.getTime();
    }

    // 获得本周最后一天，周六
    public static Date getWeekEndDate() {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    // 获得上周第一天
    public static Date getLastWeekStartDate() {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 1);
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }

    // 获得上周最后一天
    public static Date getLastWeekEndDate() {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 7);
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }
}
