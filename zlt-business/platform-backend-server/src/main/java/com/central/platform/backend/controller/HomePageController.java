package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.game.dto.HomePageDto;
import com.central.game.feign.GameService;
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

    @ApiOperation(value = "会员报表查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "parent", value = "所属平台", required = false),
    })
    @GetMapping("/find")
    public Result<HomePageVo> find(@RequestParam("parent")String parent){
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
}
