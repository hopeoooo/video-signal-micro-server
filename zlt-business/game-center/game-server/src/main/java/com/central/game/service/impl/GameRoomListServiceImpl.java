package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.redis.constant.RedisKeyConstant;
import com.central.common.redis.template.RedisRepository;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.constants.PlayEnum;
import com.central.game.mapper.GameRoomListMapper;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.GameRoomList;
import com.central.game.model.vo.GameRoomListVo;
import com.central.game.model.vo.LivePotVo;
import com.central.game.service.IGameLotteryResultService;
import com.central.game.service.IGameRecordService;
import com.central.game.service.IGameRoomInfoOfflineService;
import com.central.game.service.IGameRoomListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@CacheConfig(cacheNames = {"gameRoomList"})
public class GameRoomListServiceImpl extends SuperServiceImpl<GameRoomListMapper, GameRoomList> implements IGameRoomListService {

    @Autowired
    private GameRoomListMapper gameRoomListMapper;
    @Autowired
    private IGameRoomInfoOfflineService gameRoomInfoOfflineService;
    @Autowired
    private IGameRecordService gameRecordService;
    @Autowired
    private IGameLotteryResultService gameLotteryResultService;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public List<GameRoomList> findGameRoomList(Long gameId) {
        LambdaQueryWrapper<GameRoomList> lqw = Wrappers.lambdaQuery();
        if (gameId != null) {
            lqw.eq(GameRoomList::getGameId, gameId);
        }
        lqw.orderByAsc(GameRoomList::getId);
        List<GameRoomList> gameRoomLists = gameRoomListMapper.selectList(lqw);
        return gameRoomLists;
    }

    @Override
    @CacheEvict(key = "#id")
    public Boolean updateRoomStatus(Long id, Integer roomStatus, String maintainStart, String maintainEnd) {
        GameRoomList gameRoomList = gameRoomListMapper.selectById(id);
        if (gameRoomList == null) {
            return false;
        }
        Date maintainStartTemp = null;
        Date maintainEndTemp = null;
        gameRoomList.setRoomStatus(roomStatus);
        if (roomStatus == 2) {
            if (maintainStart == null || maintainEnd == null) {
                return false;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                maintainStartTemp = sdf.parse(maintainStart);
                maintainEndTemp = sdf.parse(maintainEnd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        gameRoomList.setMaintainStart(maintainStartTemp);
        gameRoomList.setMaintainEnd(maintainEndTemp);
        gameRoomListMapper.updateById(gameRoomList);
        return true;
    }

    @Override
    @Cacheable(key = "#id")
    public GameRoomList findById(String id) {
        return gameRoomListMapper.selectById(id);
    }

    @Override
    public List<GameRoomListVo> findRoomListByGameId(Long gameId) {
        LambdaQueryWrapper<GameRoomList> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameRoomList::getGameId, gameId).ne(GameRoomList::getRoomStatus, 0);
        List<GameRoomList> gameRoomLists = gameRoomListMapper.selectList(lqw);
        //查询昨天列表上下两部分数据
        List<GameRoomListVo> list = new ArrayList<>();
        for (GameRoomList roomList : gameRoomLists) {
            GameRoomListVo vo = new GameRoomListVo();
            BeanUtils.copyProperties(roomList, vo);
            vo.setTableNum(roomList.getGameRoomName());
            //桌台中心信息
            GameRoomInfoOffline tableCoreInfo = getTableCoreInfo(vo);
            if (tableCoreInfo != null) {
                vo.setBootNum(tableCoreInfo.getBootNum());
                vo.setBureauNum(tableCoreInfo.getBureauNum());
                vo.setSecond(tableCoreInfo.getSecond());
                vo.setCurrentSecond(tableCoreInfo.getCurrentSecond());
                vo.setStatus(tableCoreInfo.getStatus());
                //桌台上部分信息
                getTableUpInfo(vo);
                //桌台下部分信息
                getTableLowerInfo(vo);
            }
            list.add(vo);
        }
        return list;
    }

    public GameRoomInfoOffline getTableCoreInfo(GameRoomListVo vo) {
        GameRoomInfoOffline infoOffline = gameRoomInfoOfflineService.lambdaQuery().eq(GameRoomInfoOffline::getGameId, vo.getGameId())
                .eq(GameRoomInfoOffline::getTableNum, vo.getTableNum())
                .orderByDesc(GameRoomInfoOffline::getCreateTime)
                .last("limit 1").one();
        //计算实时倒计时
        if (infoOffline != null && infoOffline.getTimes() != null && infoOffline.getSecond() != null) {
            //数据修改后距离当前时间过了多少秒
            long second = (System.currentTimeMillis() - infoOffline.getTimes()) / 1000;
            long differ = infoOffline.getSecond() - second;
            Long currentSecond = differ > 0 ? differ : 0;
            infoOffline.setCurrentSecond(currentSecond.intValue());
        }
        return infoOffline;
    }

    public void getTableUpInfo(GameRoomListVo vo) {
        List<LivePotVo> livePotVoList = gameRecordService.getBureauNumLivePot(vo.getGameId(), vo.getTableNum(), vo.getBootNum(), vo.getBureauNum());
        BigDecimal totalBetAmount = BigDecimal.ZERO;
        for (LivePotVo livePotVo : livePotVoList) {
            totalBetAmount = totalBetAmount.add(livePotVo.getBetAmount());
            //庄
            if (PlayEnum.BAC_BANKER.getCode().equals(livePotVo.getBetCode())) {
                vo.setBaccaratBetNum(livePotVo.getBetNum());
                vo.setBaccaratBetAmonut(livePotVo.getBetAmount());
            }
            //闲
            if (PlayEnum.BAC_PLAYER.getCode().equals(livePotVo.getBetCode())) {
                vo.setPlayerBetNum(livePotVo.getBetNum());
                vo.setPlayerBetAmonut(livePotVo.getBetAmount());
            }
        }
        vo.setTotalBetAmonut(totalBetAmount);
        String groupId = vo.getGameId() + "-" + vo.getTableNum() + "-" + vo.getBootNum() + "-" + vo.getBureauNum();
        //统计本局下注人数(去重)
        String redisBetNumDataKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_BET_NUM_DATA + groupId;
        Long totalBetNum = redisRepository.sGetSetSize(redisBetNumDataKey);
        vo.setTotalBetNum(totalBetNum.intValue());
    }

    public void getTableLowerInfo(GameRoomListVo vo) {
        //本靴牌开奖结果
        List<GameLotteryResult> lotteryResultList = gameLotteryResultService.lambdaQuery()
                .eq(GameLotteryResult::getGameId, vo.getGameId())
                .eq(GameLotteryResult::getTableNum, vo.getTableNum())
                .eq(GameLotteryResult::getBootNum, vo.getBootNum()).list();
        if (CollectionUtils.isEmpty(lotteryResultList)) {
            return;
        }
        vo.setBootNumTotalNum(lotteryResultList.size());
        Integer bankerNum = 0;
        Integer playerNum = 0;
        Integer tieNum = 0;
        for (GameLotteryResult result : lotteryResultList) {
            if (result.getResult().contains(PlayEnum.BAC_BANKER.getCode())) {
                bankerNum++;
            } else if (result.getResult().contains(PlayEnum.BAC_PLAYER.getCode())) {
                playerNum++;
            } else if (result.getResult().contains(PlayEnum.BAC_TIE.getCode())) {
                tieNum++;
            }
        }
        vo.setBootNumBankerNum(bankerNum);
        vo.setBootNumPlayerNum(playerNum);
        vo.setBootNumTieNum(tieNum);
    }
}
