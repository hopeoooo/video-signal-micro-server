package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.redis.constant.RedisKeyConstant;
import com.central.common.redis.template.RedisRepository;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.common.utils.DateUtil;
import com.central.game.constants.PlayEnum;
import com.central.game.mapper.GameRoomListMapper;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.GameRoomList;
import com.central.game.model.vo.GameRoomListVo;
import com.central.game.model.vo.LivePotVo;
import com.central.game.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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
    private IGameLotteryResultService gameLotteryResultService;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private IPushGameDataToClientService pushGameDataToClientService;

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
        //异步通知客户端
        pushGameDataToClientService.syncPushGameRoomStatus(gameRoomList);
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
        //查询列表上下两部分数据
        List<GameRoomListVo> list = new ArrayList<>();
        for (GameRoomList roomList : gameRoomLists) {
            GameRoomListVo vo = new GameRoomListVo();
            BeanUtils.copyProperties(roomList, vo);
            vo.setTableNum(roomList.getGameRoomName());
            //桌台状态
            getRoomStatus(vo);
            //桌台中心信息
            GameRoomInfoOffline tableCoreInfo = gameRoomInfoOfflineService.getNewestTableInfo(vo.getGameId(),vo.getTableNum());
            if (tableCoreInfo != null) {
                //中心区域信息
                getTableCoreInfo(vo,tableCoreInfo);
                //桌台上部分信息
                getTableUpInfo(vo);
                //桌台下部分信息
                getTableLowerInfo(vo);
            }
            list.add(vo);
        }
        return list;
    }

    @Override
//    @Cacheable(key = "#p0+'::'+#p1")
    public GameRoomList findByGameIdAndGameRoomName(Long gameId, String gameRoomName) {
        LambdaQueryWrapper<GameRoomList> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameRoomList::getGameId, gameId);
        lqw.eq(GameRoomList::getGameRoomName, gameRoomName);
        GameRoomList gameRoomList = gameRoomListMapper.selectOne(lqw);
        return gameRoomList;
    }

    public void getRoomStatus(GameRoomListVo vo) {
        //判断桌台维护状态
        if (2 == vo.getRoomStatus()) {
            boolean maintain = DateUtil.isEffectiveDate(new Date(), vo.getMaintainStart(), vo.getMaintainEnd());
            //当前时间不在维护时间区间内属于正常状态
            if (!maintain) {
                vo.setRoomStatus(1);
                vo.setMaintainStart(null);
                vo.setMaintainEnd(null);
            }
        }
    }

    public void getTableCoreInfo(GameRoomListVo vo, GameRoomInfoOffline tableCoreInfo) {
        vo.setBootNum(tableCoreInfo.getBootNum());
        vo.setBureauNum(tableCoreInfo.getBureauNum());
        vo.setSecond(tableCoreInfo.getSecond());
        vo.setCurrentSecond(tableCoreInfo.getCurrentSecond());
        vo.setStatus(tableCoreInfo.getStatus());
    }

    public void getTableUpInfo(GameRoomListVo vo) {
        String groupId = vo.getGameId() + "-" + vo.getTableNum() + "-" + vo.getBootNum() + "-" + vo.getBureauNum();
        String redisDataKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_DATA + groupId;
        BigDecimal totalBetAmount = BigDecimal.ZERO;
        //所有玩法
        List<PlayEnum> playList = PlayEnum.getPlayListByGameId(vo.getGameId());
        for (PlayEnum playEnum : playList) {
            Integer betNum = 0;
            BigDecimal betAmount = BigDecimal.ZERO;
            Object redisLivePot = redisRepository.getHashValues(redisDataKey, playEnum.getCode());
            if (!ObjectUtils.isEmpty(redisLivePot)) {
                LivePotVo livePot = (LivePotVo) redisLivePot;
                betNum = livePot.getBetNum();
                betAmount = livePot.getBetAmount();
            }
            totalBetAmount = totalBetAmount.add(betAmount);
            //庄
            if (PlayEnum.BAC_BANKER.getCode().equals(playEnum.getCode())) {
                vo.setBaccaratBetNum(betNum);
                vo.setBaccaratBetAmount(betAmount);
                //闲
            } else if (PlayEnum.BAC_PLAYER.getCode().equals(playEnum.getCode())) {
                vo.setPlayerBetNum(betNum);
                vo.setPlayerBetAmount(betAmount);
            }
        }
        vo.setTotalBetAmount(totalBetAmount);
        //统计本局下注人数(去重)
        String redisBetNumDataKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_BET_NUM_DATA + groupId;
        Long totalBetNum = redisRepository.sGetSetSize(redisBetNumDataKey);
        vo.setTotalBetNum(totalBetNum.intValue());
    }

    public void getTableLowerInfo(GameRoomListVo vo) {
        //本靴牌开奖结果
        List<GameLotteryResult> lotteryResultList = gameLotteryResultService.getBootNumResultList(vo.getGameId(), vo.getTableNum(), vo.getBootNum());
        if (CollectionUtils.isEmpty(lotteryResultList)) {
            return;
        }
        gameLotteryResultService.setLotteryNum(lotteryResultList, vo);
    }
}
