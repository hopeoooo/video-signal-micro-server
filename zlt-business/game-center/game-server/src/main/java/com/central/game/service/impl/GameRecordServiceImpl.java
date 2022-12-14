package com.central.game.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.constant.CommonConstant;
import com.central.common.model.*;
import com.central.common.redis.constant.RedisKeyConstant;
import com.central.common.redis.lock.RedissLockUtil;
import com.central.common.redis.template.RedisRepository;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.common.utils.DateUtil;
import com.central.common.utils.I18nUtil;
import com.central.config.dto.TouristDto;
import com.central.config.feign.ConfigService;
import com.central.game.constants.GameListEnum;
import com.central.game.constants.GameRoomInfoOfflineStatusEnum;
import com.central.game.constants.PlayEnum;
import com.central.game.dto.*;
import com.central.game.mapper.GameRecordMapper;
import com.central.game.model.GameList;
import com.central.game.model.GameRecord;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.GameRoomList;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRecordBetDataCo;
import com.central.game.model.co.GameRecordBetPageCo;
import com.central.game.model.co.GameRecordCo;
import com.central.game.model.vo.*;
import com.central.game.rocketMq.constant.BindingNameConstant;
import com.central.game.service.*;
import com.central.user.feign.UserService;
import com.central.user.model.vo.RankingListVo;
import com.central.user.model.vo.SysUserMoneyVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@Service
public class GameRecordServiceImpl extends SuperServiceImpl<GameRecordMapper, GameRecord> implements IGameRecordService {

    @Autowired
    private GameRecordMapper gameRecordMapper;
    @Autowired
    private IGameRoomListService gameRoomListService;
    @Autowired
    private IGameListService gameListService;
    @Autowired
    private UserService userService;
    @Autowired
    private IPushGameDataToClientService pushGameDataToClientService;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private IGameRoomInfoOfflineService gameRoomInfoOfflineService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private StreamBridge streamBridge;

    private static final String minLimitRed = "minLimitRed";
    private static final String maxLimitRed = "maxLimitRed";
    //??????????????????????????????
    private static final Integer disableBigSmall = 31;

    @Override
    @Transactional
    public Result<List<LivePotVo>> saveRecord(GameRecordCo co, SysUser user, String ip) {
        //??????????????????
        user = userService.selectByUsername(user.getUsername());
        String userName = user.getUsername();
        Long gameId = co.getGameId();
        Result checkGameResult = checkGame(gameId);
        if (checkGameResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return checkGameResult;
        }
        GameList gameList = (GameList) checkGameResult.getDatas();
        String tableNum = co.getTableNum();
        Result checkTableResult = checkTable(gameId, tableNum);
        if (checkTableResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return checkTableResult;
        }
        GameRoomList gameRoomList = (GameRoomList) checkTableResult.getDatas();
        GameRoomInfoOffline infoOffline = gameRoomInfoOfflineService.getNewestTableInfo(gameId, tableNum);
        if (infoOffline == null || infoOffline.getStatus() != GameRoomInfoOfflineStatusEnum.START_BETTING.getStatus()) {
            return Result.failed("???????????????");
        }
        String bootNum = infoOffline.getBootNum();
        String bureauNum = infoOffline.getBureauNum();
        List<GameRecordBetDataCo> betResult = co.getBetResult();
        BigDecimal totalBetAmount = BigDecimal.ZERO;
        for (GameRecordBetDataCo betDataCo : betResult) {
            PlayEnum playEnum = PlayEnum.getPlayByCode(betDataCo.getBetCode());
            if (playEnum == null) {
                return Result.failed("???????????????");
            }
            //????????????31?????????????????????????????????
            if (Integer.parseInt(bureauNum) >= disableBigSmall && (PlayEnum.BAC_BIG.getCode().equals(betDataCo.getBetCode()) || PlayEnum.BAC_SMALL.getCode().equals(betDataCo.getBetCode()))) {
                return Result.failed("??????????????????????????????");
            }
            BigDecimal betAmount = new BigDecimal(betDataCo.getBetAmount());
            if (betAmount.compareTo(BigDecimal.ZERO) < 1) {
                return Result.failed(betDataCo.getBetName() + "????????????????????????0");
            }
            totalBetAmount = totalBetAmount.add(betAmount);
        }
        //???????????????????????????????????????
        if (UserType.APP_GUEST.name().equals(user.getType())) {
            Result<TouristDto> touristAmount = configService.findTouristAmount();
            if (touristAmount.getResp_code() != CodeEnum.SUCCESS.getCode()) {
                log.error("?????????????????????????????????userName={},touristAmount={}", userName, touristAmount);
                return Result.failed("????????????");
            }
            TouristDto datas = touristAmount.getDatas();
            if (datas != null && datas.getTouristSingleMaxBet() != null && totalBetAmount.compareTo(datas.getTouristSingleMaxBet()) == 1) {
                return Result.failedDynamic("???????????????????????????????????????,??????????????????????????????", datas.getTouristSingleMaxBet().stripTrailingZeros().toPlainString());
            }
        }
        //??????????????????????????????????????????????????????
        Result<SysUserMoneyVo> totalMoneyResult = userService.getMoneyByUserName(userName);
        if (totalMoneyResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            log.error("???????????????????????????userName={},totalMoneyResult={}", userName, totalMoneyResult);
            return Result.failed("????????????");
        }
        SysUserMoneyVo userMoneyVo = totalMoneyResult.getDatas();
        if (totalBetAmount.compareTo(userMoneyVo.getMoney()) == 1) {
            return Result.failed("????????????");
        }
        String livePotLockKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_LOCK + gameId + "-" + tableNum + "-" + bootNum + "-" + bureauNum;
        boolean livePotLock = RedissLockUtil.tryLock(livePotLockKey, RedisKeyConstant.WAIT_TIME, RedisKeyConstant.LEASE_TIME);
        if (!livePotLock) {
            return Result.failed("????????????");
        }
        //???????????????????????????
        List<LivePotVo> newAddBetList = new ArrayList<>();
        //????????????????????????(??????)
        NewAddLivePotVo newAddLivePotVo = null;
        try {
            //????????????
            for (GameRecordBetDataCo betDataCo : betResult) {
                Result checkLimitRedResult = checkLimitRed(gameRoomList, betDataCo, gameId, tableNum, bootNum, bureauNum);
                if (checkLimitRedResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
                    return checkLimitRedResult;
                }
            }
            //????????????????????????????????????????????????
            List<GameRecord> gameRecords = getBeforeBureauNum(user.getId(), gameId, co.getTableNum(), bootNum, bureauNum);
            //???????????????????????????????????????
            for (GameRecordBetDataCo betDataCo : betResult) {
                boolean flag = false;
                List<GameRecord> gameRecordList = new LinkedList<>();
                List<GameRecord> failGameRecordList = new LinkedList<>();
                for (GameRecord record : gameRecords) {
                    BigDecimal newBetAmount = new BigDecimal(betDataCo.getBetAmount());
                    if (record.getGameId().equals(gameId.toString()) && record.getBetCode().equals(betDataCo.getBetCode())) {
                        record.setBetAmount(record.getBetAmount().add(newBetAmount));
                        //??????????????????
                        Map<String, BigDecimal> minMaxLimitRed = getMinMaxLimitRed(gameRoomList, betDataCo.getBetCode());
                        if (!CollectionUtils.isEmpty(minMaxLimitRed)) {
                            record.setMinLimitRed(minMaxLimitRed.get(minLimitRed));
                            record.setMaxLimitRed(minMaxLimitRed.get(maxLimitRed));
                        }
                        //??????????????????
                        Result<SysUserMoney> moneyResult = userService.transterMoney(user.getId(), newBetAmount, null, CapitalEnum.BET.getType(), null, record.getBetId(), null);
                        //?????????????????????????????????????????????
                        if (moneyResult.getResp_code() == CodeEnum.SUCCESS.getCode()) {
                            gameRecordList.add(record);
                            //????????????????????????
                            newAddBetList.add(getLivePotVo(user, betDataCo.getBetCode(), betDataCo.getBetName(), newBetAmount, 0));
                        } else {
                            failGameRecordList.add(record);
                        }
                    }
                }
                if(CollUtil.isNotEmpty(gameRecordList)){
                    gameRecordMapper.updateBatch(gameRecordList);
                    flag = true;
                }
                if(CollUtil.isNotEmpty(failGameRecordList)){
                    log.error("????????????????????????????????????gameRecordList={}", JSONUtil.toJsonStr(failGameRecordList));
                }
                //???????????????????????????
                if (!flag) {
                    //?????????????????????
                    GameRecord record = getGameRecord(co, gameRoomList, betDataCo, user, gameId, gameList.getName(), bootNum, bureauNum, ip);
                    Result<SysUserMoney> moneyResult = userService.transterMoney(user.getId(), record.getBetAmount(), null, CapitalEnum.BET.getType(), null, record.getBetId(), null);
                    //?????????????????????????????????????????????
                    if (moneyResult.getResp_code() == CodeEnum.SUCCESS.getCode()) {
                        gameRecordMapper.insert(record);
                        //????????????????????????
                        newAddBetList.add(getLivePotVo(user, betDataCo.getBetCode(), betDataCo.getBetName(), record.getBetAmount(), 1));
                    } else {
                        log.error("????????????????????????????????????moneyResult={},record={}", moneyResult.toString(), record.toString());
                    }
                }
            }
            //??????????????????
            newAddLivePotVo = summaryLivePot(gameId, tableNum, bootNum, bureauNum, newAddBetList);
        } finally {
            RedissLockUtil.unlock(livePotLockKey);
        }
        //?????????????????? ????????????
        pushGameDataToClientService.syncLivePot(newAddLivePotVo);
        //??????????????????????????????????????????,?????????user????????????
//        pushGameDataToClientService.syncTableNumGroup(newAddLivePotVo);
        return Result.succeed(newAddBetList);
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    public Map<String, BigDecimal> getMinMaxLimitRed(GameRoomList gameRoomList, String betCode) {
        Map<String, BigDecimal> limitRed = new HashMap<>();
        //?????????????????????
        if (PlayEnum.BAC_BANKER.getCode().equals(betCode) || PlayEnum.BAC_PLAYER.getCode().equals(betCode) || PlayEnum.BAC_BIG.getCode().equals(betCode) || PlayEnum.BAC_SMALL.getCode().equals(betCode)) {
            limitRed.put(minLimitRed, gameRoomList.getMinBankerPlayer());
            limitRed.put(maxLimitRed, gameRoomList.getMaxBankerPlayer());
        } else if (PlayEnum.BAC_TIE.getCode().equals(betCode)) {
            limitRed.put(minLimitRed, gameRoomList.getMinSum());
            limitRed.put(maxLimitRed, gameRoomList.getMaxSum());
        } else if (PlayEnum.BAC_BPAIR.getCode().equals(betCode) || PlayEnum.BAC_PPAIR.getCode().equals(betCode)) {
            limitRed.put(minLimitRed, gameRoomList.getMinTwain());
            limitRed.put(maxLimitRed, gameRoomList.getMaxTwain());
        }
        return limitRed;
    }


    public LivePotVo getLivePotVo(SysUser user, String betDataCo, String betName, BigDecimal newAddBetAmount, Integer onlineNum) {
        LivePotVo livePotVo = new LivePotVo();
        livePotVo.setUserId(user.getId());
        livePotVo.setUserName(user.getUsername());
        livePotVo.setBetCode(betDataCo);
        livePotVo.setBetName(betName);
        livePotVo.setBetAmount(newAddBetAmount);
        livePotVo.setBetNum(onlineNum);
        return livePotVo;
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    public List<GameRecord> getBeforeBureauNum(Long userId, Long gameId, String tableNum, String bootNum, String bureauNum) {
        LambdaQueryWrapper<GameRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameRecord::getUserId, userId);
        lqw.eq(GameRecord::getGameId, gameId);
        lqw.eq(GameRecord::getTableNum, tableNum);
        lqw.eq(GameRecord::getBootNum, bootNum);
        lqw.eq(GameRecord::getBureauNum, bureauNum);
        List<GameRecord> gameRecords = gameRecordMapper.selectList(lqw);
        return gameRecords;
    }

    public Result checkGame(Long gameId) {
        GameList gameList = gameListService.findById(gameId);
        if (gameList == null) {
            return Result.failed("?????????????????????");
        }
        if (gameList.getGameStatus() == CommonConstant.DISABLE) {
            return Result.failed("????????????????????????");
        }
        //???????????????????????????????????????????????????????????????
        if (gameList.getGameStatus() == CommonConstant.MAINTAIN) {
            boolean maintain = DateUtil.isEffectiveDate(new Date(), gameList.getMaintainStart(), gameList.getMaintainEnd());
            //?????????????????????????????????????????????????????????
            if (maintain) {
                return Result.failed("????????????????????????");
            }
            gameList.setGameStatus(CommonConstant.NORMAL);
            gameList.setMaintainStart(null);
            gameList.setMaintainEnd(null);
        }
        return Result.succeed(gameList);
    }

    public Result checkTable(Long gameId, String tableNum) {
        GameRoomList gameRoomList = gameRoomListService.findByGameIdAndGameRoomName(gameId, tableNum);
        if (gameRoomList == null) {
            return Result.failed("?????????????????????");
        }
        if (gameRoomList.getRoomStatus() == CommonConstant.DISABLE) {
            return Result.failed("????????????????????????");
        }
        //???????????????????????????????????????????????????????????????
        if (gameRoomList.getRoomStatus() == CommonConstant.MAINTAIN) {
            boolean maintain = DateUtil.isEffectiveDate(new Date(), gameRoomList.getMaintainStart(), gameRoomList.getMaintainEnd());
            //?????????????????????????????????????????????????????????
            if (maintain) {
                return Result.failed("????????????????????????");
            }
            gameRoomList.setRoomStatus(CommonConstant.NORMAL);
            gameRoomList.setMaintainStart(null);
            gameRoomList.setMaintainEnd(null);
        }
        return Result.succeed(gameRoomList);
    }

    /**
     * ????????????
     * 1.???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * 2.??????????????????????????????????????????????????????????????????????????????
     * 3.???????????????????????????????????????????????????????????????????????????????????????
     *
     * @param gameRoomList
     * @param betDataCo
     * @param gameId
     * @param tableNum
     * @param bootNum
     * @param bureauNum
     * @return
     */
    public Result checkLimitRed(GameRoomList gameRoomList, GameRecordBetDataCo betDataCo, Long gameId, String tableNum, String bootNum, String bureauNum) {
        String betCode = betDataCo.getBetCode();
        //??????????????????????????????
        Map<String, BigDecimal> minMaxLimitRed = getMinMaxLimitRed(gameRoomList, betCode);
        if (CollectionUtils.isEmpty(minMaxLimitRed)) {
            return Result.succeed();
        }
        BigDecimal minLimitRedAmount = minMaxLimitRed.get(minLimitRed);
        BigDecimal maxLimitRedAmount = minMaxLimitRed.get(maxLimitRed);
        if (minLimitRedAmount == null || maxLimitRedAmount == null) {
            return Result.succeed();
        }
        //?????????????????????
        BigDecimal limitBetAmount = getBureauNumLimitBetAmount(gameId, tableNum, bootNum, bureauNum, betCode);
        BigDecimal betAmount = new BigDecimal(betDataCo.getBetAmount());
        BigDecimal totalBetAmount = limitBetAmount.add(betAmount);
        //??????????????????
        if (totalBetAmount.compareTo(minLimitRedAmount) == -1) {
            return Result.failed(betDataCo.getBetName() + "???????????????????????????????????????");
        }
        //??????????????????
        if (totalBetAmount.compareTo(maxLimitRedAmount) == 1) {
            return Result.failed(betDataCo.getBetName() + "???????????????????????????????????????");
        }
        return Result.succeed();

    }

    /**
     * ????????????????????????
     *
     * @param betCode
     * @param gameId
     * @param tableNum
     * @param bootNum
     * @param bureauNum
     * @return
     */
    public BigDecimal getBureauNumLimitBetAmount(Long gameId, String tableNum, String bootNum, String bureauNum, String betCode) {
        String groupId = gameId + "-" + tableNum + "-" + bootNum + "-" + bureauNum;
        String redisDataKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_DATA + groupId;
        //?????????
        if (PlayEnum.BAC_BANKER.getCode().equals(betCode) || PlayEnum.BAC_PLAYER.getCode().equals(betCode)) {
            Object banker = redisRepository.getHashValues(redisDataKey, PlayEnum.BAC_BANKER.getCode());
            Object player = redisRepository.getHashValues(redisDataKey, PlayEnum.BAC_PLAYER.getCode());
            BigDecimal absBetAmount = getAbsBetAmount(banker, player);
            return absBetAmount;
            //??????
        } else if (PlayEnum.BAC_BIG.getCode().equals(betCode) || PlayEnum.BAC_SMALL.getCode().equals(betCode)) {
            Object big = redisRepository.getHashValues(redisDataKey, PlayEnum.BAC_BIG.getCode());
            Object small = redisRepository.getHashValues(redisDataKey, PlayEnum.BAC_SMALL.getCode());
            BigDecimal absBetAmount = getAbsBetAmount(big, small);
            return absBetAmount;
        } else if (PlayEnum.BAC_TIE.getCode().equals(betCode)) {
            Object tie = redisRepository.getHashValues(redisDataKey, PlayEnum.BAC_TIE.getCode());
            if (ObjectUtils.isEmpty(tie)) {
                return BigDecimal.ZERO;
            }
            return ((LivePotVo) tie).getBetAmount();
        } else if (PlayEnum.BAC_BPAIR.getCode().equals(betCode) || PlayEnum.BAC_PPAIR.getCode().equals(betCode)) {
            Object bpairPpair = redisRepository.getHashValues(redisDataKey, betCode);
            if (ObjectUtils.isEmpty(bpairPpair)) {
                return BigDecimal.ZERO;
            }
            return ((LivePotVo) bpairPpair).getBetAmount();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getAbsBetAmount(Object bankerBig, Object playerSmall) {
        BigDecimal bankerBigBetAmount = BigDecimal.ZERO;
        BigDecimal playerSmallBetAmount = BigDecimal.ZERO;
        if (!ObjectUtils.isEmpty(bankerBig)) {
            bankerBigBetAmount = ((LivePotVo) bankerBig).getBetAmount();
        }
        if (!ObjectUtils.isEmpty(playerSmall)) {
            playerSmallBetAmount = ((LivePotVo) playerSmall).getBetAmount();
        }
        BigDecimal absAmount = bankerBigBetAmount.subtract(playerSmallBetAmount).abs();
        return absAmount;
    }

    public GameRecord getGameRecord(GameRecordCo co, GameRoomList gameRoomList, GameRecordBetDataCo betDataCo, SysUser user, Long gameId, String gameName, String bootNum, String bureauNum, String ip) {
        GameRecord gameRecord = new GameRecord();
        gameRecord.setTableNum(co.getTableNum());
        gameRecord.setBootNum(bootNum);
        gameRecord.setBureauNum(bureauNum);
        gameRecord.setUserId(user.getId());
        gameRecord.setUserName(user.getUsername());
        gameRecord.setParent(user.getParent());
        gameRecord.setUserType(user.getType());
        gameRecord.setGameId(gameId.toString());
        gameRecord.setGameName(gameName);
        gameRecord.setBetCode(betDataCo.getBetCode());
        gameRecord.setBetName(betDataCo.getBetName());
        gameRecord.setBetAmount(new BigDecimal(betDataCo.getBetAmount()));
        gameRecord.setBetTime(new Date());
        gameRecord.setIp(ip);
        //?????????
        String betId = getBetId(gameId, co.getTableNum(), bootNum, bureauNum);
        gameRecord.setBetId(betId);
        //??????????????????
        Map<String, BigDecimal> minMaxLimitRed = getMinMaxLimitRed(gameRoomList, betDataCo.getBetCode());
        if (!CollectionUtils.isEmpty(minMaxLimitRed)) {
            gameRecord.setMinLimitRed(minMaxLimitRed.get(minLimitRed));
            gameRecord.setMaxLimitRed(minMaxLimitRed.get(maxLimitRed));
        }
        return gameRecord;
    }

    /**
     * ????????????????????????
     *
     * @param gameId        ??????
     * @param tableNum      ??????
     * @param bootNum       ??????
     * @param bureauNum     ??????
     * @param newAddBetList ???????????????????????????
     */
    public NewAddLivePotVo summaryLivePot(Long gameId, String tableNum, String bootNum, String bureauNum, List<LivePotVo> newAddBetList) {
        if (CollectionUtils.isEmpty(newAddBetList)) {
            return null;
        }
        NewAddLivePotVo newAddLivePotVo = new NewAddLivePotVo();
        newAddLivePotVo.setGameId(gameId);
        newAddLivePotVo.setTableNum(tableNum);
        newAddLivePotVo.setBootNum(bootNum);
        newAddLivePotVo.setBureauNum(bureauNum);
        newAddLivePotVo.setBetResult(newAddBetList);
        String groupId = gameId + "-" + tableNum + "-" + bootNum + "-" + bureauNum;
        String redisDataKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_DATA + groupId;
        //????????????????????????
        String redisBetNumDataKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_BET_NUM_DATA + groupId;
        long beforeBetNum = redisRepository.sGetSetSize(redisBetNumDataKey);
        BigDecimal totalBetAmount = BigDecimal.ZERO;
        for (LivePotVo vo : newAddBetList) {
            totalBetAmount = totalBetAmount.add(vo.getBetAmount());
            LivePotVo livePotVo = (LivePotVo) redisRepository.getHashValues(redisDataKey, vo.getBetCode());
            if (ObjectUtils.isEmpty(livePotVo)) {
                livePotVo = new LivePotVo();
                BeanUtils.copyProperties(vo, livePotVo);
            } else {
                livePotVo.setBetAmount(livePotVo.getBetAmount().add(vo.getBetAmount()));
                livePotVo.setBetNum(livePotVo.getBetNum() + vo.getBetNum());
            }
            //???????????????????????????????????????????????????
            Long expire = 5 * 60L;
            redisRepository.putHashValue(redisDataKey, vo.getBetCode(), livePotVo);
            redisRepository.setExpire(redisDataKey, expire);
            redisRepository.sSetAndTime(redisBetNumDataKey, expire, vo.getUserName());
        }
        long afterBetNum = redisRepository.sGetSetSize(redisBetNumDataKey);
        Long newAddBetNum = afterBetNum - beforeBetNum;
        newAddLivePotVo.setBetNum(newAddBetNum.intValue());
        newAddLivePotVo.setBetAmount(totalBetAmount);
        return newAddLivePotVo;
    }

    /**
     * ??????????????????????????????????????????+??????+??????+??????+?????????+???????????????  ??????????????????
     *
     * @param gameId    ??????ID
     * @param tableNum  ??????
     * @param bootNum   ??????
     * @param bureauNum ??????
     */
    public String getBetId(Long gameId, String tableNum, String bootNum, String bureauNum) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateTime = formatter.format(new Date());
        String randomStr = "";
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int j = random.nextInt(10);
            randomStr = randomStr + j;
        }
        String abbreviation = GameListEnum.getAbbreviationByGameId(gameId);
        String betId = abbreviation + dateTime + tableNum + bootNum + bureauNum + randomStr;
        return betId;
    }

    /**
     * ??????
     *
     * @return
     */
    @Override
    public PageResult<GameRecordBackstageVo> findList(GameRecordBetCo params) {
        Page<GameRecordBackstageVo> page = new Page<>(params.getPage(), params.getLimit());
        List<GameRecordBackstageVo> list = baseMapper.findGameRecordList(page, params);
        return PageResult.<GameRecordBackstageVo>builder().data(list).count(page.getTotal()).build();
    }

    @Override
    public GameRecordDto findGameRecordTotal(GameRecordBetCo params) {
        return baseMapper.findGameRecordTotal(params);
    }

    @Override
    public GameRecordReportDto findBetAmountTotal(Map<String, Object> params) {
        return baseMapper.findBetAmountTotal(params);
    }

    @Override
    public GameRecordReportDto findValidbetTotal(Map<String, Object> params) {
        return baseMapper.findValidbetTotal(params);
    }

    @Override
    public GameRecordReportDto findWinningAmountTotal(Map<String, Object> params) {
        return baseMapper.findWinningAmountTotal(params);
    }

    @Override
    public List<GameRecord> getGameRecordByParent(GameRecordBetCo params) {
        return gameRecordMapper.getGameRecordByParent(params);
    }

    @Override
    public HomePageDto findHomePageDto(String parent) {
        return gameRecordMapper.findHomePageDto(parent);
    }

    @Override
    public List<LivePotVo> getBureauNumLivePot(Long gameId, String tableNum, String bootNum, String bureauNum) {
        return gameRecordMapper.getLivePot(gameId, tableNum, bootNum, bureauNum);
    }

    @Override
    public NewAddLivePotVo getLivePot(Long gameId, String tableNum) {
        NewAddLivePotVo vo = new NewAddLivePotVo();
        GameRoomInfoOffline roomInfoOffline = gameRoomInfoOfflineService.getNewestTableInfo(gameId, tableNum);
        String bootNum = null;
        String bureauNum = null;
        if (roomInfoOffline != null) {
            bootNum = roomInfoOffline.getBootNum();
            bureauNum = roomInfoOffline.getBureauNum();
        }
        vo.setGameId(gameId);
        vo.setTableNum(tableNum);
        vo.setBootNum(bootNum);
        vo.setBureauNum(bureauNum);
        List<LivePotVo> voList = new ArrayList<>();
        String redisDataKey = null;
        if (roomInfoOffline != null) {
            String groupId = gameId + "-" + tableNum + "-" + bootNum + "-" + bureauNum;
            redisDataKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_DATA + groupId;
        }
        //????????????
        List<PlayEnum> playList = PlayEnum.getPlayListByGameId(gameId);
        for (PlayEnum playEnum : playList) {
            LivePotVo livePot = null;
            if (StringUtils.isNotBlank(redisDataKey)) {
                livePot = (LivePotVo) redisRepository.getHashValues(redisDataKey, playEnum.getCode());
            }
            if (livePot == null) {
                livePot = new LivePotVo();
                livePot.setBetCode(playEnum.getCode());
                livePot.setBetName(playEnum.getName());
            }
            voList.add(livePot);
        }
        vo.setBetResult(voList);
        return vo;
    }

    @Override
    public HomeHistogramDto findHomeHistogramDto(Map<String, Object> params) {
        return gameRecordMapper.findHomeHistogramDto(params);
    }

    @Override
    public PageResult<GameRecordVo> findBetList(GameRecordBetPageCo params) {
        String startTime = DateUtil.getStartTime(0);
        String endTime = DateUtil.getEndTime(0);
        String type = params.getType();
        if ("1".equals(type)) {
            startTime = DateUtil.getSimpleDateFormat().format(DateUtil.getWeekStartDate());
            endTime = DateUtil.getSimpleDateFormat().format(DateUtil.getWeekEndDate());
        } else if ("2".equals(type)) {
            startTime = DateUtil.getLastWeekMonday();
            endTime = DateUtil.getLastWeekSunday();
        }
        params.setStartTime(startTime);
        params.setEndTime(endTime);
        Page<GameRecordVo> page = new Page<>(params.getPage(), params.getLimit());
        List<GameRecordVo> list = gameRecordMapper.findBetList(page, params);
        if (!CollectionUtils.isEmpty(list)) {
            //??????
            BigDecimal subtotalBetAmount = BigDecimal.ZERO;
            BigDecimal subtotalValidbet = BigDecimal.ZERO;
            BigDecimal subtotalWinLoss = BigDecimal.ZERO;
            for (GameRecordVo vo : list) {
                if (!ObjectUtils.isEmpty(vo.getBetAmount())) {
                    subtotalBetAmount = subtotalBetAmount.add(vo.getBetAmount());
                }
                if (!ObjectUtils.isEmpty(vo.getValidbet())) {
                    subtotalValidbet = subtotalValidbet.add(vo.getValidbet());
                }
                if (!ObjectUtils.isEmpty(vo.getWinLoss())) {
                    subtotalWinLoss = subtotalWinLoss.add(vo.getWinLoss());
                }
            }
            GameRecordVo subtotal = new GameRecordVo();
            subtotal.setGameName(I18nUtil.t("??????"));
            subtotal.setBetAmount(subtotalBetAmount);
            subtotal.setValidbet(subtotalValidbet);
            subtotal.setWinLoss(subtotalWinLoss);
            list.add(subtotal);
            //??????
            GameRecordVo totalBetList = gameRecordMapper.findTotalBetList(params);
            if (totalBetList != null) {
                totalBetList.setGameName(I18nUtil.t("??????"));
                list.add(totalBetList);
            }
        }
        return PageResult.<GameRecordVo>builder().data(list).count(page.getTotal()).build();
    }

    @Override
    public List<GameRecord> getGameRecordByBureauNum(Long gameId, String tableNum) {
        GameRoomInfoOffline roomInfoOffline = gameRoomInfoOfflineService.getNewestTableInfo(gameId, tableNum);
        if (roomInfoOffline == null || (roomInfoOffline.getStatus() != GameRoomInfoOfflineStatusEnum.START_BETTING.getStatus() && roomInfoOffline.getStatus() != GameRoomInfoOfflineStatusEnum.END_BETTING.getStatus())) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<GameRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameRecord::getGameId, gameId);
        lqw.eq(GameRecord::getTableNum, tableNum);
        lqw.eq(GameRecord::getBootNum, roomInfoOffline.getBootNum());
        lqw.eq(GameRecord::getBureauNum, roomInfoOffline.getBureauNum());
        List<GameRecord> gameRecords = gameRecordMapper.selectList(lqw);
        return gameRecords;
    }

    @Override
    public List<PayoutResultVo> getPayoutResult(Long gameId, String tableNum, String bootNum, String bureauNum) {
        return gameRecordMapper.getPayoutResult(gameId, tableNum, bootNum, bureauNum);

    }

    @Override
    public List<RankingListVo> getTodayLotteryList() {
        String startTime = DateUtil.getStartTime(0);
        String endTime = DateUtil.getEndTime(0);
        List<RankingListVo> todayLotteryList = gameRecordMapper.getTodayLotteryList(startTime, endTime);
        for (RankingListVo vo : todayLotteryList) {
            vo.setUserName(setUserNameAsterisk(vo.getUserName()));
        }
        return todayLotteryList;
    }

    @Override
    public List<RankingListVo> getTodayBetList() {
        String startTime = DateUtil.getStartTime(0);
        String endTime = DateUtil.getEndTime(0);
        List<RankingListVo> todayBetList = gameRecordMapper.getTodayBetList(startTime, endTime);
        for (RankingListVo vo : todayBetList) {
            vo.setUserName(setUserNameAsterisk(vo.getUserName()));
        }
        return todayBetList;
    }

    private String setUserNameAsterisk(String userName) {
        if (ObjectUtils.isEmpty(userName)) {
            return null;
        }
        String benStr = userName.substring(0, 2);
        String endStr = userName.substring(userName.length() - 2);
        return benStr + "***" + endStr;
    }

    @Override
    public String getTodayValidbet(Long userId) {
        String startTime = DateUtil.getStartTime(0);
        String endTime = DateUtil.getEndTime(0);
        BigDecimal todayValidbet = gameRecordMapper.getTodayValidbet(userId, startTime, endTime);
        return keepDecimal(todayValidbet).toString();
    }

    @Override
    public String getTotalValidbet(Long userId) {
        BigDecimal totalValidbet = gameRecordMapper.getTotalValidbet(userId);
        return keepDecimal(totalValidbet).toString();
    }

    @Override
    public List<GameWinningRateVo> getGameWinningRate(Long userId) {
        List<GameWinningRateVo> list = new ArrayList<>();
        GameWinningRateVo vo = null;
        for (GameListEnum gameListEnum : GameListEnum.values()) {
            vo = new GameWinningRateVo();
            vo.setGameId(gameListEnum.getGameId());
            vo.setGameName(gameListEnum.getGameName());
            list.add(vo);
            List<BigDecimal> totalList = gameRecordMapper.getGameWinningRate(userId, gameListEnum.getGameId());
            if (CollectionUtils.isEmpty(totalList)) {
                vo.setRete("0");
                continue;
            }
            //?????????
            int totalNum = totalList.size();
            //?????????
            List<BigDecimal> winList = totalList.stream().filter(t -> !ObjectUtils.isEmpty(t) && t.compareTo(BigDecimal.ZERO) == 1).collect(Collectors.toList());
            int winNum = winList.size();
            //??????
            BigDecimal rate = new BigDecimal(winNum).divide(new BigDecimal(totalNum), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            vo.setRete(keepDecimal1(rate).toString());
        }
        return list;
    }

    @Override
    public List<GameRecord> getNewestBetListByGameId(Long gameId, Long userId) {
        List<GameRecord> gameRecordList = new ArrayList<>();
        Result<LoginLog> loginResult = userService.getLastLoginLogByUserId(userId);
        if (loginResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            log.error("?????????????????????????????????????????????userId={}", userId);
            return gameRecordList;
        }
        LoginLog loginLog = loginResult.getDatas();
        if (loginLog == null || ObjectUtils.isEmpty(loginLog.getCreateTime())) {
            return gameRecordList;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String loginTime = sdf.format(loginLog.getCreateTime());
        //??????????????????????????????
        LambdaQueryWrapper<GameRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameRecord::getGameId, gameId);
        lqw.eq(GameRecord::getUserId, userId);
        lqw.ge(GameRecord::getCreateTime, loginTime);
        lqw.orderByDesc(GameRecord::getCreateTime);
        lqw.last("limit 1");
        GameRecord gameRecord = gameRecordMapper.selectOne(lqw);
        if (gameRecord == null) {
            return gameRecordList;
        }
        //????????????????????????????????????
        LambdaQueryWrapper<GameRecord> lqw1 = Wrappers.lambdaQuery();
        lqw1.eq(GameRecord::getUserId, userId);
        lqw1.eq(GameRecord::getGameId, gameId);
        lqw1.eq(GameRecord::getTableNum, gameRecord.getTableNum());
        lqw1.eq(GameRecord::getBootNum, gameRecord.getBootNum());
        lqw1.eq(GameRecord::getBureauNum, gameRecord.getBureauNum());
        gameRecordList = gameRecordMapper.selectList(lqw1);
        return gameRecordList;
    }

    @Override
    @Async
    public void calculateWashCode(GameRecord record) {
        if (!ObjectUtils.isEmpty(record.getValidbet()) && record.getValidbet().compareTo(BigDecimal.ZERO) == 1) {
            log.info("[calculateWashCode][????????????????????????,????????????={}]", record.toString());
            boolean sendResult = streamBridge.send(BindingNameConstant.WASH_CODE, record);
            log.info("[calculateWashCode][????????????????????????,????????????={}, ?????? = {}]", record.toString(), sendResult);
        }
    }

    @Override
    @Async
    public void calculateFlowCode(GameRecord record) {
        if (!ObjectUtils.isEmpty(record.getValidbet()) && record.getValidbet().compareTo(BigDecimal.ZERO) == 1) {
            log.info("[calculateFlowCode][????????????????????????,????????????={}]", record.toString());
            boolean sendResult = streamBridge.send(BindingNameConstant.FLOW_CODE, record);
            log.info("[calculateFlowCode][????????????????????????,????????????={}, ?????? = {}]", record.toString(), sendResult);
        }
    }

    @Override
    @Async
    public void syncDeleteGuestRecordBureauNum(Long gameId, String tableNum, String bootNum, String bureauNum) {
        gameRecordMapper.deleteGuestRecordBureauNum(UserType.APP_GUEST.name(), gameId, tableNum, bootNum, bureauNum);
    }

    @Override
    public List<UserReportDto> findUserReportDto(Map<String, Object> params) {
        return gameRecordMapper.findUserReportDto(params);
    }

    private BigDecimal keepDecimal(BigDecimal val) {
        return val == null ? BigDecimal.ZERO.setScale(2) : val.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal keepDecimal1(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val.setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public List<RankingBackstageVo> findValidBetRankingList(List<Long> listId) {
        return gameRecordMapper.findValidBetRankingList(listId);
    }

    @Override
    public List<RankingBackstageVo> findWinLossRankingList() {
        return gameRecordMapper.findWinLossRankingList();
    }

    @Override
    public List<UserGameReportDto> findUserGameReportDto(Long userId) {
        return gameRecordMapper.findUserGameReportDto(userId);
    }

    @Override
    public void clearGuestGameRecord(Long userId) {
        gameRecordMapper.clearGuestGameRecord(userId);
    }
}
