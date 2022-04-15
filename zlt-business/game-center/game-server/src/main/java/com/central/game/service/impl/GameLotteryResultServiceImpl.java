package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.*;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.common.utils.DateUtil;
import com.central.game.constants.PlayEnum;
import com.central.game.mapper.GameLotteryResultMapper;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRecord;
import com.central.game.model.GameRecordSon;
import com.central.game.model.GameRoomList;
import com.central.game.model.co.GameLotteryResultBackstageCo;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.game.model.vo.GameRoomListVo;
import com.central.game.model.vo.LivePotVo;
import com.central.game.service.IGameLotteryResultService;
import com.central.game.service.IGameRecordService;
import com.central.game.service.IGameRecordSonService;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import com.central.user.feign.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
    private IGameRecordSonService gameRecordSonService;

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
//    @Transactional
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
            String betCode = record.getBetCode();
            if (result.contains(betCode)) {
                PlayEnum playEnum = PlayEnum.getPlayByCode(betCode);
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
            //如果是庄闲开和退换本金
            } else {
                record.setWinningAmount(BigDecimal.ZERO);
                record.setWinLoss(betAmount.negate());
            }
            record.setSetTime(setTime);
            //输赢金额大于0，更新本地钱包
            if (record.getWinLoss().compareTo(BigDecimal.ZERO) == 1) {
                Result<SysUserMoney> moneyResult = userService.transterMoney(record.getUserId(), record.getWinLoss(), null, CapitalEnum.SETTLEMENTAMOUNT.getType(), null, record.getBetId());
                if (moneyResult.getResp_code() == CodeEnum.SUCCESS.getCode()) {
                    writeBackAddMoneyStatus(record.getId());
                } else {
                    log.error("用户钱包加派彩金额失败,moneyResult={},gameRecord={}", moneyResult.toString(), record.toString());
                }
            //开奖结果包含和 庄闲玩法退回本金
            } else if (result.contains(PlayEnum.BAC_TIE.getCode()) && (PlayEnum.BAC_BANKER.getCode().equals(betCode)) || PlayEnum.BAC_PLAYER.getCode().equals(betCode)) {
                Result<SysUserMoney> moneyResult = userService.transterMoney(record.getUserId(), record.getBetAmount(), null, CapitalEnum.SETTLEMENTAMOUNT.getType(), null, record.getBetId());
                if (moneyResult.getResp_code() == CodeEnum.SUCCESS.getCode()) {
                    writeBackAddMoneyStatus(record.getId());
                } else {
                    log.error("开奖结果包含和退回本金失败,moneyResult={},gameRecord={}", moneyResult.toString(), record.toString());
                }
            }
            //计算有效投注额
            BigDecimal validbet = calculateValidbet(record);
            record.setValidbet(validbet);
            //更新下注记录
            gameRecordService.updateById(record);
            //计算洗码（游客不计算）
            if (!UserType.APP_GUEST.name().equals(record.getUserType())) {
                gameRecordService.calculateWashCode(record);
            }
        }
    }

    public void writeBackAddMoneyStatus(Long gameRecordId){
        //回写状态
        GameRecordSon gameRecordSon = gameRecordSonService.lambdaQuery().eq(GameRecordSon::getGameRecordId, gameRecordId).one();
        if (gameRecordSon == null) {
            gameRecordSon = new GameRecordSon();
            gameRecordSon.setGameRecordId(gameRecordId);
            gameRecordSon.setAddMoneyStatus(1);
        }
        gameRecordSonService.saveOrUpdate(gameRecordSon);
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
                } else if (lotteryResult.contains(PlayEnum.BAC_TIE.getCode())) { //游戏结果是和局，有效投注额=0
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

    @Override
    public void setLotteryNum(List<GameLotteryResult> lotteryResultList, GameRoomListVo vo){
        vo.setBootNumResultList(lotteryResultList);
        vo.setBootNumTotalNum(lotteryResultList.size());
        Integer bankerNum = 0;
        Integer playerNum = 0;
        Integer tieNum = 0;
        Integer bpairNum = 0;
        Integer ppairNum = 0;
        for (GameLotteryResult result : lotteryResultList) {
            if (result.getResult().contains(PlayEnum.BAC_BANKER.getCode())) {
                bankerNum++;
            }
            if (result.getResult().contains(PlayEnum.BAC_PLAYER.getCode())) {
                playerNum++;
            }
            if (result.getResult().contains(PlayEnum.BAC_TIE.getCode())) {
                tieNum++;
            }
            if (result.getResult().contains(PlayEnum.BAC_BPAIR.getCode())) {
                bpairNum++;
            }
            if (result.getResult().contains(PlayEnum.BAC_PPAIR.getCode())) {
                ppairNum++;
            }
        }
        vo.setBootNumBankerNum(bankerNum);
        vo.setBootNumPlayerNum(playerNum);
        vo.setBootNumTieNum(tieNum);
        vo.setBootNumBpairNum(bpairNum);
        vo.setBootNumPpairNum(ppairNum);
    }

    @Override
    public List<GameLotteryResult> getBootNumResultList(Long gameId, String tableNum, String bootNum) {
        LambdaQueryWrapper<GameLotteryResult> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameLotteryResult::getGameId, gameId);
        lqw.eq(GameLotteryResult::getTableNum, tableNum);
        lqw.eq(GameLotteryResult::getBootNum, bootNum);
        List<GameLotteryResult> lotteryResultList = gameLotteryResultMapper.selectList(lqw);
        return lotteryResultList;
    }
}
