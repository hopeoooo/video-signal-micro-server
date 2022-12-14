package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.constant.CommonConstant;
import com.central.common.constant.I18nKeys;
import com.central.common.model.Result;
import com.central.common.redis.constant.RedisKeyConstant;
import com.central.common.redis.lock.RedissLockUtil;
import com.central.common.redis.template.RedisRepository;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.common.utils.DateUtil;
import com.central.common.utils.ServletUtil;
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

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public void updateRoomStatus(Map<String, Object> params) {
        this.baseMapper.updateRoomStatus(params);
    }

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
        //????????????????????????
        if (!ObjectUtils.isEmpty(gameRoomList.getRoomStatus()) && gameRoomList.getRoomStatus() == CommonConstant.MAINTAIN) {
            boolean maintain = DateUtil.isEffectiveDate(new Date(), gameRoomList.getMaintainStart(), gameRoomList.getMaintainEnd());
            //?????????????????????????????????????????????????????????
            if (!maintain) {
                gameRoomList.setRoomStatus(CommonConstant.NORMAL);
                gameRoomList.setMaintainStart(null);
                gameRoomList.setMaintainEnd(null);
            }
        }
        //?????????????????????
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
        //?????????????????????????????????
        List<GameRoomListVo> list = new ArrayList<>();
        //????????????????????????????????????
        List<RoomFollowList> roomLists = roomFollowListService.lambdaQuery().eq(RoomFollowList::getUserId, userId).list();
        for (GameRoomList roomList : gameRoomLists) {
            GameRoomListVo vo = new GameRoomListVo();
            BeanUtils.copyProperties(roomList, vo);
            vo.setRoomId(roomList.getId());
            vo.setTableNum(roomList.getGameRoomName());
            //??????????????????????????????
            for (RoomFollowList roomFollow : roomLists) {
                if (roomList.getId() == roomFollow.getRoomId()) {
                    vo.setFollowStatus(1);
                    break;
                }
            }
            //???????????????????????????
            String i18nTableNum = getI18nTableNum(roomList);
            vo.setI18nTableNum(i18nTableNum);
            //??????????????????
            setTabelInfo(vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * ???????????????????????????
     * @param roomList
     */
    @Override
    public String getI18nTableNum(GameRoomList roomList) {
        if (roomList == null) {
            return null;
        }
        //???????????????
        HttpServletRequest request = ServletUtil.getHttpServletRequest();
        String language = request.getHeader(I18nKeys.LANGUAGE);
        if (I18nKeys.Locale.ZH_CN.equalsIgnoreCase(language)) {
            return roomList.getGameRoomName();
        } else if (I18nKeys.Locale.KHM.equalsIgnoreCase(language)) {
            return roomList.getKhmName();
        } else if (I18nKeys.Locale.TH.equalsIgnoreCase(language)) {
            return roomList.getThName();
        }
        return roomList.getEnName();
    }

    @Override
    public GameRoomListVo setTabelInfo(GameRoomListVo vo){
        //???????????????????????????
        //????????????
        setRoomStatus(vo);
        //??????????????????
        GameRoomInfoOffline tableCoreInfo = gameRoomInfoOfflineService.getNewestTableInfo(vo.getGameId(),vo.getTableNum());
        if (tableCoreInfo != null) {
            //??????????????????
            getTableCoreInfo(vo,tableCoreInfo);
            //?????????????????????
            getTableUpInfo(vo);
            //?????????????????????
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
        //????????????????????????
        if (!ObjectUtils.isEmpty(vo.getRoomStatus()) && vo.getRoomStatus() == CommonConstant.MAINTAIN) {
            boolean maintain = DateUtil.isEffectiveDate(new Date(), vo.getMaintainStart(), vo.getMaintainEnd());
            //?????????????????????????????????????????????????????????
            if (!maintain) {
                vo.setRoomStatus(CommonConstant.NORMAL);
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
        //????????????
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
            //???
            if (PlayEnum.BAC_BANKER.getCode().equals(playEnum.getCode())) {
                vo.setBaccaratBetNum(betNum);
                vo.setBaccaratBetAmount(betAmount);
                //???
            } else if (PlayEnum.BAC_PLAYER.getCode().equals(playEnum.getCode())) {
                vo.setPlayerBetNum(betNum);
                vo.setPlayerBetAmount(betAmount);
            }
        }
        vo.setTotalBetAmount(totalBetAmount);
        //????????????????????????(??????)
        String redisBetNumDataKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_BET_NUM_DATA + groupId;
        Long totalBetNum = redisRepository.sGetSetSize(redisBetNumDataKey);
        vo.setTotalBetNum(totalBetNum.intValue());
    }

    public void getTableLowerInfo(GameRoomListVo vo) {
        //?????????????????????
        String redisDataKey = RedisKeyConstant.GAME_RECORD_LOTTERY_RESULT_DATA + vo.getGameId() + "-" + vo.getTableNum() + "-" + vo.getBootNum();
        int length = redisRepository.length(redisDataKey).intValue();
        List<GameLotteryResult> lotteryResultList = new ArrayList<>();
        if (length == 0) {
            //?????????????????????2??????????????????????????????????????????????????????????????????,???????????????????????????
            lotteryResultList = gameLotteryResultService.lambdaQuery().eq(GameLotteryResult::getGameId, vo.getGameId()).eq(GameLotteryResult::getTableNum, vo.getTableNum())
                    .eq(GameLotteryResult::getBootNum, vo.getBootNum()).orderByAsc(GameLotteryResult::getCreateTime).list();
            if (CollectionUtils.isEmpty(lotteryResultList)) {
                return;
            }
            //???????????????list??????????????????????????????????????????????????????????????????????????????
            String bootLockKey = RedisKeyConstant.GAME_RECORD_LOTTERY_RESULT_LOCK + vo.getGameId() + "-" + vo.getTableNum() + "-" + vo.getBootNum();
            boolean bootLock = RedissLockUtil.tryLock(bootLockKey, RedisKeyConstant.WAIT_TIME, RedisKeyConstant.LEASE_TIME);
            if (!bootLock) {
                log.error("?????????????????????????????????????????????vo={}", vo);
                return;
            }
            try {
                boolean exists = redisRepository.exists(redisDataKey);
                if (!exists) {
                    for (GameLotteryResult result : lotteryResultList) {
                        redisRepository.rightPush(redisDataKey, result);
                    }
                    //?????????2??????
                    redisRepository.setExpire(redisDataKey, 2 * 60 * 60);
                }
            } finally {
                RedissLockUtil.unlock(bootLockKey);
            }
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
