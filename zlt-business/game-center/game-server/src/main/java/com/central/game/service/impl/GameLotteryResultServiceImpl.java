package com.central.game.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.*;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.common.utils.DateUtil;
import com.central.game.constants.PlayEnum;
import com.central.game.mapper.GameLotteryResultMapper;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameLotteryResultBackstageCo;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.game.service.IGameLotteryResultService;
import com.central.game.service.IGameRecordService;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import com.central.user.feign.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
    @Autowired
    private PushService pushService;

    @Override
    public List<GameLotteryResult> getLotteryResultList(GameLotteryResultCo co) {
        return gameLotteryResultMapper.getLotteryResultList(co);
    }

    @Override
    public PageResult<GameLotteryResult> findList(GameLotteryResultBackstageCo map) {
        Page<GameLotteryResult> page = new Page<>(map.getPage(), map.getLimit());
        List<GameLotteryResult> list = gameLotteryResultMapper.findList(page, map);
        return PageResult.<GameLotteryResult>builder().data(list).count(page.getTotal()).build();
    }

    @Override
    @Async
    public void syncPushPayoutResult(GameLotteryResult result) {
        String groupId = result.getGameId() + "-" + result.getTableNum();
        List<GameRecord> payoutResult = gameRecordService.getPayoutResult(result.getGameId(), result.getTableNum(), result.getBootNum(), result.getBureauNum());
        for (GameRecord gameRecord : payoutResult) {
            PushResult<GameRecord> pushResult = PushResult.succeed(gameRecord, SocketTypeConstant.PAYOUT_RESULT, "派彩结果信息推送成功");
            Result<String> push = pushService.sendMessageByGroupIdAndUserName(groupId, gameRecord.getUserName(), com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
            log.info("派彩结果信息推送结果:groupId={},userName={},result={}", groupId, gameRecord.getUserName(), push);
        }
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
            record.setGameResult(result);
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
            //计算有效投注额
            BigDecimal validbet = calculateValidbet(record);
            record.setValidbet(validbet);
            //更新下注记录
            gameRecordService.updateById(record);
        }
    }

    /**
     * 计算有效投注额
     *
     * @param record 下注记录
     */
    public BigDecimal calculateValidbet(GameRecord record) {
        String lotteryResult = record.getGameResult();
        //庄闲玩法特殊处理
        if (record.getBetCode().equals(PlayEnum.BAC_BANKER.getCode()) || record.getBetCode().equals(PlayEnum.BAC_PLAYER.getCode())) {
            //查询本局庄闲玩法投注记录
            List<GameRecord> recordList = gameRecordService.lambdaQuery().eq(GameRecord::getUserId, record.getUserId()).eq(GameRecord::getGameId, record.getGameId())
                    .eq(GameRecord::getTableNum, record.getTableNum()).eq(GameRecord::getBootNum, record.getBootNum())
                    .eq(GameRecord::getBureauNum, record.getBureauNum()).in(GameRecord::getBetCode, PlayEnum.BAC_BANKER.getCode(), PlayEnum.BAC_PLAYER.getCode())
                    .orderByAsc(GameRecord::getBetCode).list();
            int size = recordList.size();
            if (size == 1) {//只下了一个玩法，非对冲
                BigDecimal winLoss = record.getWinLoss();
                if (winLoss.compareTo(BigDecimal.ZERO) == 1) {//输赢大于0，有效投注额=输赢
                    return winLoss;
                } else if (winLoss.compareTo(BigDecimal.ZERO) == -1) {//输赢小于0，有效投注=投注金额
                    return record.getBetAmount();
                } else if (lotteryResult.contains(PlayEnum.BAC_TIE.getCode())) { //游戏结果是和局，游戏投注额=0
                    return BigDecimal.ZERO;
                }
            } else if (size == 2) {//同时下了庄闲，对冲
                //闲下注记录
                GameRecord player = recordList.get(0);
                //庄下注记录
                GameRecord banker = recordList.get(0);
                BigDecimal validbet = BigDecimal.ZERO;
                //闲赢 有效投注=（闲投注金额-庄投注金额的绝对值）X闲的赔率
                if (lotteryResult.contains(PlayEnum.BAC_PLAYER.getCode())) {
                    validbet = player.getBetAmount().subtract(banker.getBetAmount()).abs().multiply(PlayEnum.BAC_PLAYER.getOdds());
                    return validbet;
                    //庄赢 有效投注=（闲投注金额-庄投注金额的绝对值）X庄的赔率
                } else if (lotteryResult.contains(PlayEnum.BAC_BANKER.getCode())) {
                    validbet = player.getBetAmount().subtract(banker.getBetAmount()).abs().multiply(PlayEnum.BAC_BANKER.getOdds());
                    return validbet;
                } else if (lotteryResult.contains(PlayEnum.BAC_TIE.getCode())) {
                    return BigDecimal.ZERO;
                }
            }
        } else { //其他玩法    有效投注额=输赢绝对值，不超过投注（若超过投注，则=投注)
            BigDecimal validbet = record.getWinLoss().abs().compareTo(record.getBetAmount()) == -1 ? record.getWinLoss().abs() : record.getBetAmount();
            return validbet;
        }
        return BigDecimal.ZERO;
    }
}
