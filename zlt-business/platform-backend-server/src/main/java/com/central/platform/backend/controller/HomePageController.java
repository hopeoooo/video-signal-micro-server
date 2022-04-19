package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.dto.HomeHistogramDto;
import com.central.game.dto.HomePageDto;
import com.central.game.feign.GameService;
import com.central.game.model.vo.OperatingOverviewVo;
import com.central.game.model.vo.RankingBackstageVo;
import com.central.platform.backend.model.dto.SiteDataReportDto;
import com.central.platform.backend.model.vo.HomeHistogramVo;
import com.central.platform.backend.model.vo.HomePageVo;
import com.central.platform.backend.util.DateUtil;
import com.central.user.feign.OnlineUserService;
import com.central.user.feign.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/homePage")
@Api(tags = "首页")
public class HomePageController {

    @Resource
    private GameService gameService;

    @Resource
    private UserService userService;

    @Autowired
    private OnlineUserService onlineUserService;

    @ApiOperation(value = "首页")
    @GetMapping("/find")
    public Result<HomePageVo> find(@RequestParam(value = "parent", required = false)String parent){
        HomePageVo homePageVo = new HomePageVo();
        Result<HomePageDto> result = gameService.findHomePageDto(parent);
        HomePageDto homePageDto = result.getDatas();
        homePageVo.setBetAmountNum(homePageDto.getBetAmountNum());
        homePageVo.setValidbet(homePageDto.getValidbet());
        homePageVo.setProfitAndLoss(homePageDto.getProfitAndLoss());

        Map<String, Object> today = DateUtil.getToday();
        today.put("type","APP");
        if (!StringUtil.isBlank(parent)){
            today.put("parent",parent);
        }
        //新增人数
        Result userNum = userService.findUserNum(today);
        homePageVo.setNewUsers(userNum.getDatas()==null?0:Integer.parseInt(userNum.getDatas().toString()));
        //在线人数
        Result<Integer> integerResult = onlineUserService.queryPlayerNums();
        homePageVo.setOnlineUsers(integerResult.getDatas()==null?0:Integer.parseInt(integerResult.getDatas().toString()));

        return Result.succeed(homePageVo);
    }

    @ApiOperation(value = "首页报表柱状图")
    @GetMapping("/findHistogram")
    public Result<List<HomeHistogramVo>> findHistogram(){
        List<HomeHistogramVo> homeHistogramVos = new ArrayList<>();

        HomeHistogramVo profitAndLoss = new HomeHistogramVo();
        profitAndLoss.setType(1);
        HomeHistogramVo validbet = new HomeHistogramVo();
        validbet.setType(2);
        Result<HomeHistogramDto> today = gameService.findHomeHistogramDto(DateUtil.getToday());
        profitAndLoss.setToday(today.getDatas().getProfitAndLoss());
        validbet.setToday(today.getDatas().getValidbet());
        Result<HomeHistogramDto> yesterday = gameService.findHomeHistogramDto(DateUtil.getYesterday());
        profitAndLoss.setYesterday(yesterday.getDatas().getProfitAndLoss());
        validbet.setYesterday(yesterday.getDatas().getValidbet());
        Result<HomeHistogramDto> week = gameService.findHomeHistogramDto(DateUtil.getWeek());
        profitAndLoss.setThisWeek(week.getDatas().getProfitAndLoss());
        validbet.setThisWeek(week.getDatas().getValidbet());
        Result<HomeHistogramDto> lastWeek = gameService.findHomeHistogramDto(DateUtil.getLastWeek());
        profitAndLoss.setLastWeek(lastWeek.getDatas().getProfitAndLoss());
        validbet.setLastWeek(lastWeek.getDatas().getValidbet());
        Result<HomeHistogramDto> month = gameService.findHomeHistogramDto(DateUtil.getMonth());
        profitAndLoss.setCurrentMonth(month.getDatas().getProfitAndLoss());
        validbet.setCurrentMonth(month.getDatas().getValidbet());
        Result<HomeHistogramDto> lastMonth = gameService.findHomeHistogramDto(DateUtil.getLastMonth());
        profitAndLoss.setLastMonth(lastMonth.getDatas().getProfitAndLoss());
        validbet.setLastMonth(lastMonth.getDatas().getValidbet());
        Result<HomeHistogramDto> nearlyTwoMonths = gameService.findHomeHistogramDto(DateUtil.getNearlyTwoMonths());
        profitAndLoss.setNearlyTwoMonths(nearlyTwoMonths.getDatas().getProfitAndLoss());
        validbet.setNearlyTwoMonths(nearlyTwoMonths.getDatas().getValidbet());
        homeHistogramVos.add(profitAndLoss);
        homeHistogramVos.add(validbet);
        return Result.succeed(homeHistogramVos);
    }

    @ApiOperation(value = "运营概览")
    @GetMapping("/operatingOverview")
    public Result<Map<String, OperatingOverviewVo>> operatingOverview() {
        //充值人数，首冲人数，注册人数
        Map<String,OperatingOverviewVo> map = new HashMap<>();
        map.put("today",getSiteDataReportDto(DateUtil.getToday()));
        map.put("yesterday",getSiteDataReportDto(DateUtil.getYesterday()));
        map.put("week",getSiteDataReportDto(DateUtil.getWeek()));
        map.put("lastWeek",getSiteDataReportDto(DateUtil.getLastWeek()));
        map.put("month",getSiteDataReportDto(DateUtil.getMonth()));
        map.put("lastMonth",getSiteDataReportDto(DateUtil.getLastMonth()));
        map.put("nearlyTwoMonths",getSiteDataReportDto(DateUtil.getNearlyTwoMonths()));
        return Result.succeed(map);
    }

    private OperatingOverviewVo getSiteDataReportDto(Map<String, Object> map) {
        //充值首充人数暂时没有
        //充值 提现暂时没有
        OperatingOverviewVo overviewVo = new OperatingOverviewVo();
        Result<GameRecordReportDto> validbetTotal = gameService.findValidbetTotal(map);
        overviewVo.setValidbet(validbetTotal.getDatas().getAmount());
        overviewVo.setValidbetNum(validbetTotal.getDatas().getNum());
        overviewVo.setWinLoss(validbetTotal.getDatas().getProfit());
        return overviewVo;
    }


}
