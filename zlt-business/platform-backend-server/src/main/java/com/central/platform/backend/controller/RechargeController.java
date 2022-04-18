package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.game.feign.GameService;
import com.central.game.model.vo.RankingBackstageVo;
import com.central.user.feign.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/Recharge")
@Api(tags = "充值记录")
public class RechargeController {

    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;


    @ApiOperation(value = "充值排行")
    @GetMapping("/findRechargeRankingList")
    public Result<List<RankingBackstageVo>> findRechargeRankingList() {

        Result<List<SysTansterMoneyLogVo>> orderTypeAccountChangeList = userService.findOrderTypeAccountChangeList("5",null);
        List<Long> listId = orderTypeAccountChangeList.getDatas().stream().map(SysTansterMoneyLogVo::getUserId).collect(Collectors.toList());

        //提现
        Result<List<SysTansterMoneyLogVo>> withdrawalList = userService.findOrderTypeAccountChangeList("6",listId);
        Map<Long, SysTansterMoneyLogVo> withdrawalMap = withdrawalList.getDatas().stream().collect(Collectors.toMap(SysTansterMoneyLogVo::getUserId, SysTansterMoneyLogVo -> SysTansterMoneyLogVo));

        //投注金额
        Result<List<RankingBackstageVo>> validBetList = gameService.findValidBetRankingList(listId);
        Map<Long, RankingBackstageVo> validBetMap = validBetList.getDatas().stream().collect(Collectors.toMap(RankingBackstageVo::getUserId, RankingBackstageVo -> RankingBackstageVo));

        List<RankingBackstageVo> list=new ArrayList<>();
        orderTypeAccountChangeList.getDatas().stream().forEach(info ->{
            RankingBackstageVo rankingBackstageVo=new RankingBackstageVo();
            rankingBackstageVo.setRecharge(info.getMoney());
            rankingBackstageVo.setUserId(info.getUserId());
            rankingBackstageVo.setUserName(info.getUserName());
            //投注
            RankingBackstageVo validBetInfo = validBetMap.get(info.getUserId());
            if (validBetInfo!=null){
                rankingBackstageVo.setValidbet(validBetInfo.getValidbet());
                rankingBackstageVo.setWinLoss(validBetInfo.getWinLoss());
            }
            //提现
            SysTansterMoneyLogVo tansterMoneyLogVo = withdrawalMap.get(info.getUserId());
            if(tansterMoneyLogVo!=null){
                rankingBackstageVo.setWithdrawal(tansterMoneyLogVo.getMoney());
            }
            rankingBackstageVo.setDifference(rankingBackstageVo.getRecharge().subtract(rankingBackstageVo.getWithdrawal()));
            list.add(rankingBackstageVo);
        });
        return Result.succeed(list);
    }

}
