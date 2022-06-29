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
import com.central.game.model.RoomFollowList;
import com.central.game.model.vo.GameRoomListVo;
import com.central.game.model.vo.LivePotVo;
import com.central.game.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
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
    private RedisRepository redisRepository;
    @Autowired
    private IPushGameDataToClientService pushGameDataToClientService;
    @Autowired
    private IRoomFollowListService roomFollowListService;
    @Autowired
    @Lazy
    private IGameLotteryResultService gameLotteryResultService;

    @Override
    @CacheEvict(key = "#id")
    public Boolean deleteBy(Long id) {
        return removeById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public Boolean update(Long id, GameRoomList gameRoomList) {
        return saveOrUpdate(gameRoomList);
    }

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
        //判断桌台维护状态
        if (!ObjectUtils.isEmpty(gameRoomList.getRoomStatus()) && gameRoomList.getRoomStatus() == 2) {
            boolean maintain = DateUtil.isEffectiveDate(new Date(), gameRoomList.getMaintainStart(), gameRoomList.getMaintainEnd());
            //当前时间不在维护时间区间内属于正常状态
            if (!maintain) {
                gameRoomList.setRoomStatus(1);
                gameRoomList.setMaintainStart(null);
                gameRoomList.setMaintainEnd(null);
            }
        }
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
    public List<GameRoomListVo> findRoomListByGameId(Long gameId,Long userId) {
        LambdaQueryWrapper<GameRoomList> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameRoomList::getGameId, gameId).ne(GameRoomList::getRoomStatus, 0);
        lqw.orderByAsc(GameRoomList::getGameRoomName);
        List<GameRoomList> gameRoomLists = gameRoomListMapper.selectList(lqw);
        //查询列表上下两部分数据
        List<GameRoomListVo> list = new ArrayList<>();
        //查询当前用户桌台关注情况
        List<RoomFollowList> roomLists = roomFollowListService.lambdaQuery().eq(RoomFollowList::getUserId, userId).list();
        for (GameRoomList roomList : gameRoomLists) {
            GameRoomListVo vo = new GameRoomListVo();
            BeanUtils.copyProperties(roomList, vo);
            vo.setRoomId(roomList.getId());
            vo.setTableNum(roomList.getGameRoomName());
            //判断当前桌台是否关注
            for (RoomFollowList roomFollow : roomLists) {
                if (roomList.getId() == roomFollow.getRoomId()) {
                    vo.setFollowStatus(1);
                    break;
                }
            }
            setTabelInfo(vo);
            list.add(vo);
        }
        return list;
    }

    @Override
    public GameRoomListVo setTabelInfo(GameRoomListVo vo){
        //桌台状态
        setRoomStatus(vo);
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
        return vo;
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

    @Override
    public void setRoomStatus(GameRoomListVo vo) {
        //判断桌台维护状态
        if (!ObjectUtils.isEmpty(vo.getRoomStatus()) && vo.getRoomStatus() == 2) {
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
        String redisDataKey = RedisKeyConstant.GAME_RECORD_LOTTERY_RESULT_DATA + vo.getGameId() + "-" + vo.getTableNum() + "-" + vo.getBootNum();
        int length = redisRepository.length(redisDataKey).intValue();
        List<GameLotteryResult> lotteryResultList = new ArrayList<>();
        if (length == 0) {
            //缓存过期时间为2小时，防止换靴后长时间没有新开靴，查不到数据
            lotteryResultList = gameLotteryResultService.lambdaQuery().eq(GameLotteryResult::getGameId, vo.getGameId()).eq(GameLotteryResult::getTableNum, vo.getTableNum())
                    .eq(GameLotteryResult::getBootNum, vo.getBootNum()).orderByAsc(GameLotteryResult::getCreateTime).list();
            if (CollectionUtils.isEmpty(lotteryResultList)) {
                return;
            }
            for (GameLotteryResult result : lotteryResultList) {
                redisRepository.rightPush(redisDataKey, result);
            }
            //有效期2小时
            redisRepository.setExpire(redisDataKey, 2 * 60 * 60);
        } else {
            List<Object> list = redisRepository.getList(redisDataKey, 0, length);
            lotteryResultList = (List<GameLotteryResult>) (List) list;
        }
        setLotteryNum(lotteryResultList, vo);
    }

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
}
