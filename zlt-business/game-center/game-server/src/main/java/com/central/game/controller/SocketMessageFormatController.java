package com.central.game.controller;

import com.central.common.model.PushResult;
import com.central.game.model.GameRecord;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.vo.LotteryResultVo;
import com.central.game.model.vo.NewAddLivePotVo;
import com.central.game.model.vo.PayoutResultVo;
import com.central.push.constant.SocketTypeConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@RestController
@RequestMapping("/socket")
@Api(tags = "Socket推送数据消息格式,仅用于查看数据格式方便客户端对接")
public class SocketMessageFormatController {

    @ApiOperation(value = "即时彩池Socket数据格式")
    @GetMapping("/getLivePot")
    public PushResult<NewAddLivePotVo> getLivePot() {
        NewAddLivePotVo newAddLivePotVo = new NewAddLivePotVo();
        PushResult<NewAddLivePotVo> pushResult = PushResult.succeed(newAddLivePotVo, SocketTypeConstant.LIVE_POT, "即时彩池数据送成功");
        return pushResult;
    }

    @ApiOperation(value = "派彩结果Socket数据格式")
    @GetMapping("/getPayoutResult")
    public PushResult<PayoutResultVo> getPayoutResult() {
        PayoutResultVo payoutResultVo = new PayoutResultVo();
        PushResult<PayoutResultVo> pushResult = PushResult.succeed(payoutResultVo, SocketTypeConstant.PAYOUT_RESULT, "派彩结果信息推送成功");
        return pushResult;
    }

    @ApiOperation(value = "开奖结果Socket数据格式")
    @GetMapping("/getLotterResult")
    public PushResult<LotteryResultVo> getLotterResult() {
        LotteryResultVo vo = new LotteryResultVo();
        PushResult<LotteryResultVo> pushResult = PushResult.succeed(vo, SocketTypeConstant.LOTTER_RESULT, "桌台开奖信息推送成功");
        return pushResult;
    }

    @ApiOperation(value = "桌台基本信息Socket数据格式")
    @GetMapping("/getTableInfo")
    public PushResult<GameRoomInfoOffline> getTableInfo() {
        GameRoomInfoOffline po = new GameRoomInfoOffline();
        PushResult<GameRoomInfoOffline> pushResult = PushResult.succeed(po, SocketTypeConstant.TABLE_INFO, "桌台配置信息推送成功");
        return pushResult;
    }

}
