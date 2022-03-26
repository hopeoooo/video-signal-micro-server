package com.central.game.service.impl;

import com.central.common.model.CapitalEnum;
import com.central.common.model.CodeEnum;
import com.central.common.model.Result;
import com.central.common.model.SysUserMoney;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.common.utils.DateUtil;
import com.central.game.constants.PlayEnum;
import com.central.game.mapper.GameLotteryResultMapper;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.game.service.IGameLotteryResultService;
import com.central.game.service.IGameRecordService;
import com.central.user.feign.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@Service
public class GameLotteryResultServiceImpl extends SuperServiceImpl<GameLotteryResultMapper, GameLotteryResult> implements IGameLotteryResultService {

    @Autowired
    private GameLotteryResultMapper gameLotteryResultMapper;
    @Autowired
    private IGameRecordService gameRecordService;
    @Autowired
    private UserService userService;

    @Override
    public List<GameLotteryResult> getLotteryResultList(GameLotteryResultCo co) {
        return gameLotteryResultMapper.getLotteryResultList(co);
    }

    @Override
    @Transactional
    public void calculateBetResult(GameLotteryResult lotteryResult) {
        SimpleDateFormat df = DateUtil.getSimpleDateFormat();
        Date setTime = null;
        try {
            setTime = df.parse(lotteryResult.getLotteryTime());
        } catch (ParseException e) {
            log.error("结算时间格式错误，lotteryResult={}", lotteryResult.toString());
            e.printStackTrace();
        }
        //先查询本局的下注记录
        List<GameRecord> gameRecordList = gameRecordService.lambdaQuery().eq(GameRecord::getTableNum, lotteryResult.getTableNum())
                .eq(GameRecord::getBootNum, lotteryResult.getBootNum()).eq(GameRecord::getBureauNum, lotteryResult.getBureauNum()).list();
        //计算每笔注单的派彩金额和输赢值
        String result = lotteryResult.getResult();
        for (GameRecord record : gameRecordList) {
            BigDecimal betAmount = record.getBetAmount();
            record.setGameResult(lotteryResult.getResult());
            record.setGameResultName(lotteryResult.getResultName());
            //包含则说明中奖
            if (result.contains(record.getBetCode())) {
                PlayEnum playEnum = PlayEnum.getPlayByCode(record.getBetCode());
                if (playEnum == null) {
                    log.error("玩法不支持,lotteryResult={}", lotteryResult.toString());
                    break;
                }
                //派彩金额=下注金额*（赔率+1）
                BigDecimal winningAmount = betAmount.multiply(playEnum.getOdds().add(BigDecimal.ONE));
                record.setWinningAmount(winningAmount);
                //输赢金额=派彩金额-下注金额
                BigDecimal winLoss = winningAmount.subtract(betAmount);
                record.setWinLoss(winLoss);
            } else {
                record.setWinningAmount(BigDecimal.ZERO);
                record.setWinLoss(betAmount.negate());
            }
            record.setSetTime(setTime);
            //输赢金额大于0，更新本地钱包
            if (record.getWinLoss().compareTo(BigDecimal.ZERO) == 1) {
                Result<SysUserMoney> moneyResult = userService.transterMoney(record.getUserId(), record.getWinLoss(), null, CapitalEnum.SETTLEMENTAMOUNT.getType(), null, record.getBetId());
                if (moneyResult.getResp_code() == CodeEnum.SUCCESS.getCode()) {
                    record.setAddMoneyStatus(1);
                } else {
                    log.error("用户钱包加派彩金额失败,moneyResult={},gameRecord={}", moneyResult.toString(), record.toString());
                }
            }
            //更新下注记录
            gameRecordService.updateById(record);
        }
    }
}
