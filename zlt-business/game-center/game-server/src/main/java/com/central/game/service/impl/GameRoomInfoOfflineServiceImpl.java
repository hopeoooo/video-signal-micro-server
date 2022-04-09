package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.redis.constant.RedisKeyConstant;
import com.central.common.redis.template.RedisRepository;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.constants.PlayEnum;
import com.central.game.mapper.GameRoomInfoOfflineMapper;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.vo.GameRoomInfoOfflineVo;
import com.central.game.model.vo.LivePotVo;
import com.central.game.service.IGameLotteryResultService;
import com.central.game.service.IGameRecordService;
import com.central.game.service.IGameRoomInfoOfflineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@Service
public class GameRoomInfoOfflineServiceImpl extends SuperServiceImpl<GameRoomInfoOfflineMapper, GameRoomInfoOffline> implements IGameRoomInfoOfflineService {

    @Autowired
    private GameRoomInfoOfflineMapper gameRoomInfoOfflineMapper;
    @Autowired
    private IGameLotteryResultService gameLotteryResultService;
    @Autowired
    private IGameRecordService gameRecordService;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public GameRoomInfoOffline findByGameIdAndTableNumAndBootNumAndBureauNum(String gameId, String tableNum, String bootNum, String bureauNum) {
        LambdaQueryWrapper<GameRoomInfoOffline> qw = Wrappers.lambdaQuery();
        qw.eq(GameRoomInfoOffline::getGameId, gameId)
                .eq(GameRoomInfoOffline::getTableNum, tableNum)
                .eq(GameRoomInfoOffline::getBootNum, bootNum)
                .eq(GameRoomInfoOffline::getBureauNum, bureauNum);
        GameRoomInfoOffline infoOffline = gameRoomInfoOfflineMapper.selectOne(qw);
        return infoOffline;
    }

    @Override
    public GameRoomInfoOffline getNewestTableInfo(Long gameId, String tableNum) {
        LambdaQueryWrapper<GameRoomInfoOffline> qw = Wrappers.lambdaQuery();
        qw.eq(GameRoomInfoOffline::getGameId, gameId)
                .eq(GameRoomInfoOffline::getTableNum, tableNum)
                .orderByDesc(GameRoomInfoOffline::getCreateTime)
                .last("limit 1");
        GameRoomInfoOffline infoOffline = gameRoomInfoOfflineMapper.selectOne(qw);
        return infoOffline;
    }

    @Override
    public GameRoomInfoOfflineVo getNewestTableInfoVo(Long gameId, String tableNum) {
        GameRoomInfoOfflineVo infoOfflineVo=new GameRoomInfoOfflineVo();
        LambdaQueryWrapper<GameRoomInfoOffline> qw = Wrappers.lambdaQuery();
        qw.eq(GameRoomInfoOffline::getGameId, gameId)
                .eq(GameRoomInfoOffline::getTableNum, tableNum)
                .orderByDesc(GameRoomInfoOffline::getCreateTime)
                .last("limit 1");
        GameRoomInfoOffline infoOffline = gameRoomInfoOfflineMapper.selectOne(qw);
        //计算实时倒计时
        if (infoOffline != null && infoOffline.getTimes() != null && infoOffline.getSecond() != null) {
            //数据修改后距离当前时间过了多少秒
            long second = (System.currentTimeMillis() - infoOffline.getTimes()) / 1000;
            long differ = infoOffline.getSecond() - second;
            Long currentSecond = differ > 0 ? differ : 0;
            infoOffline.setCurrentSecond(currentSecond.intValue());
        }
        BeanUtils.copyProperties(infoOffline,infoOfflineVo);
        //统计桌台上半部分信息
        getTableUpInfo(infoOfflineVo);
        //统计桌台下半部分信息
        getTableLowerInfo(infoOfflineVo);
        return infoOfflineVo;
    }

    public void getTableUpInfo(GameRoomInfoOfflineVo infoOfflineVo){
        List<LivePotVo> livePotVoList = gameRecordService.getBureauNumLivePot(infoOfflineVo.getGameId(), infoOfflineVo.getTableNum(), infoOfflineVo.getBootNum(), infoOfflineVo.getBureauNum());
        BigDecimal totalBetAmount = BigDecimal.ZERO;
        for(LivePotVo livePotVo:livePotVoList){
            totalBetAmount=totalBetAmount.add(livePotVo.getBetAmount());
            //庄
            if (PlayEnum.BAC_BANKER.getCode().equals(livePotVo.getBetCode())){
                infoOfflineVo.setBaccaratBetNum(livePotVo.getBetNum());
                infoOfflineVo.setBaccaratBetAmonut(livePotVo.getBetAmount());
            }
            //闲
            if (PlayEnum.BAC_PLAYER.getCode().equals(livePotVo.getBetCode())){
                infoOfflineVo.setPlayerBetNum(livePotVo.getBetNum());
                infoOfflineVo.setPlayerBetAmonut(livePotVo.getBetAmount());
            }
        }
        infoOfflineVo.setTotalBetAmonut(totalBetAmount);
        String groupId = infoOfflineVo.getGameId() + "-" + infoOfflineVo.getTableNum() + "-" + infoOfflineVo.getBootNum() + "-" + infoOfflineVo.getBureauNum();
        //统计本局下注人数(去重)
        String redisBetNumDataKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_BET_NUM_DATA + groupId;
        Long totalBetNum = redisRepository.sGetSetSize(redisBetNumDataKey);
        infoOfflineVo.setTotalBetNum(totalBetNum.intValue());
    }

    public void getTableLowerInfo(GameRoomInfoOfflineVo infoOfflineVo){
        //本靴牌开奖结果
        List<GameLotteryResult> lotteryResultList = gameLotteryResultService.lambdaQuery().eq(GameLotteryResult::getGameId, infoOfflineVo.getGameId()).eq(GameLotteryResult::getTableNum, infoOfflineVo.getTableNum())
                .eq(GameLotteryResult::getBootNum, infoOfflineVo.getBootNum()).list();
        if (CollectionUtils.isEmpty(lotteryResultList)){
            return;
        }
        infoOfflineVo.setBootNumTotalNum(lotteryResultList.size());
        Integer bankerNum=0;
        Integer playerNum=0;
        Integer tieNum=0;
        for (GameLotteryResult result:lotteryResultList){
            if (result.getResult().contains(PlayEnum.BAC_BANKER.getCode())){
                bankerNum++;
            }else if (result.getResult().contains(PlayEnum.BAC_PLAYER.getCode())){
                playerNum++;
            }else if (result.getResult().contains(PlayEnum.BAC_TIE.getCode())){
                tieNum++;
            }
        }
        infoOfflineVo.setBootNumBankerNum(bankerNum);
        infoOfflineVo.setBootNumPlayerNum(playerNum);
        infoOfflineVo.setBootNumTieNum(tieNum);
    }
}
