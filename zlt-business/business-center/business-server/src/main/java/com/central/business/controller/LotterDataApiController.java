package com.central.business.controller;

import com.central.business.constant.BusinessConstants;
import com.central.business.model.co.GameLotteryResultCo;
import com.central.business.utils.CheckSignatureUtil;
import com.central.common.model.Result;
import com.central.game.feign.GameService;
import com.central.game.model.GameLotteryResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/" + BusinessConstants.LOTTER_DATA_API_URL)
@Api(tags = "比赛数据接口")
public class LotterDataApiController {

    @Autowired
    private GameService gameService;

    @GetMapping("/getLotteryResultList")
    @ApiOperation(value = "查询开奖结果记录")
    public Result<List<GameLotteryResult>> getLotteryResultList(@Valid @ModelAttribute GameLotteryResultCo co) {
        if (!CheckSignatureUtil.checkSignature(co)) {
            return Result.failed("参数签名校验失败");
        }
        com.central.game.model.co.GameLotteryResultCo params = new com.central.game.model.co.GameLotteryResultCo();
        BeanUtils.copyProperties(co, params);
        Result<List<GameLotteryResult>> result = gameService.getLotteryResultList(params);
        return result;
    }

}
