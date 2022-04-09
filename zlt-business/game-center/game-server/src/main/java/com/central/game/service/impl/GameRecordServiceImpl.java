package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.*;
import com.central.common.redis.constant.RedisKeyConstant;
import com.central.common.redis.lock.RedissLockUtil;
import com.central.common.redis.template.RedisRepository;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.common.utils.DateUtil;
import com.central.game.constants.GameListEnum;
import com.central.game.constants.PlayEnum;
import com.central.game.dto.GameRecordDto;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.dto.HomeHistogramDto;
import com.central.game.dto.HomePageDto;
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
import com.central.game.service.*;
import com.central.user.feign.UserService;
import com.central.user.model.vo.RankingListVo;
import com.central.user.model.vo.SysUserMoneyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String minLimitRed = "minLimitRed";
    private static final String maxLimitRed = "maxLimitRed";

    @Override
    @Transactional
    public Result<List<LivePotVo>> saveRecord(GameRecordCo co, SysUser user, String ip) {
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
        if (infoOffline == null || infoOffline.getStatus() != 1) {
            return Result.failed("已停止下注");
        }
        String bootNum = infoOffline.getBootNum();
        String bureauNum = infoOffline.getBureauNum();
        List<GameRecordBetDataCo> betResult = co.getBetResult();
        BigDecimal totalBetAmount = BigDecimal.ZERO;
        for (GameRecordBetDataCo betDataCo : betResult) {
            PlayEnum playEnum = PlayEnum.getPlayByCode(betDataCo.getBetCode());
            if (playEnum == null) {
                return Result.failed(betDataCo.getBetName() + "玩法不支持");
            }
            BigDecimal betAmount = new BigDecimal(betDataCo.getBetAmount());
            if (betAmount.compareTo(BigDecimal.ZERO) < 1) {
                return Result.failed(betDataCo.getBetName() + "下注金额必须大于0");
            }
            totalBetAmount = totalBetAmount.add(betAmount);
        }
        //校验本次总下注额是否超过本地剩余额度
        Result<SysUserMoneyVo> totalMoneyResult = userService.getMoneyByUserName(user.getUsername());
        if (totalMoneyResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return Result.failed("下注失败");
        }
        SysUserMoneyVo userMoneyVo = totalMoneyResult.getDatas();
        if (totalBetAmount.compareTo(userMoneyVo.getMoney()) == 1) {
            return Result.failed("余额不足");
        }
        String livePotLockKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_LOCK + gameId + "-" + tableNum + "-" + bootNum + "-" + bureauNum;
        boolean livePotLock = RedissLockUtil.tryLock(livePotLockKey, RedisKeyConstant.WAIT_TIME, RedisKeyConstant.LEASE_TIME);
        //本次下注成功的数据
        List<LivePotVo> newAddBetList = new ArrayList<>();
        if (livePotLock) {
            try {
                //限红校验
                for (GameRecordBetDataCo betDataCo : betResult) {
                    Result checkLimitRedResult = checkLimitRed(gameRoomList, betDataCo, gameId, tableNum, bootNum, bureauNum);
                    if (checkLimitRedResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
                        return checkLimitRedResult;
                    }
                }
                //先查询上次用户本局保存的注单数据
                List<GameRecord> gameRecords = getBeforeBureauNum(user.getId(), gameId, co.getTableNum(), bootNum, bureauNum);
                //同一种玩法投注额变化时更新
                for (GameRecordBetDataCo betDataCo : betResult) {
                    boolean flag = false;
                    for (GameRecord record : gameRecords) {
                        BigDecimal newBetAmount = new BigDecimal(betDataCo.getBetAmount());
                        if (record.getGameId().equals(gameId.toString()) && record.getBetCode().equals(betDataCo.getBetCode())) {
                            record.setBetAmount(record.getBetAmount().add(newBetAmount));
                            //设置限红范围
                            Map<String, BigDecimal> minMaxLimitRed = getMinMaxLimitRed(gameRoomList, betDataCo.getBetCode());
                            if (!CollectionUtils.isEmpty(minMaxLimitRed)) {
                                record.setMinLimitRed(minMaxLimitRed.get(minLimitRed));
                                record.setMaxLimitRed(minMaxLimitRed.get(maxLimitRed));
                            }
                            //扣减本地余额
                            Result<SysUserMoney> moneyResult = userService.transterMoney(user.getId(), newBetAmount, null, CapitalEnum.BET.getType(), null, record.getBetId());
                            //本地余额扣减成功，更新投注记录
                            if (moneyResult.getResp_code() == CodeEnum.SUCCESS.getCode()) {
                                gameRecordMapper.updateById(record);
                                //汇总新增的下注额
                                newAddBetList.add(getLivePotVo(betDataCo.getBetCode(), betDataCo.getBetName(), newBetAmount, 0));
                            } else {
                                log.error("投注时本地余额扣减失败，moneyResult={},record={}", moneyResult.toString(), record.toString());
                            }
                            flag = true;
                            break;
                        }
                    }
                    //之前没有保存的新增
                    if (!flag) {
                        //先扣减本地余额
                        GameRecord record = getGameRecord(co, gameRoomList, betDataCo, user, gameId, gameList.getName(), bootNum, bureauNum, ip);
                        Result<SysUserMoney> moneyResult = userService.transterMoney(user.getId(), record.getBetAmount(), null, CapitalEnum.BET.getType(), null, record.getBetId());
                        //本地余额扣减成功，保存投注记录
                        if (moneyResult.getResp_code() == CodeEnum.SUCCESS.getCode()) {
                            gameRecordMapper.insert(record);
                            //汇总新增的下注额
                            newAddBetList.add(getLivePotVo(betDataCo.getBetCode(), betDataCo.getBetName(), record.getBetAmount(), 1));
                        } else {
                            log.error("投注时本地余额扣减失败，moneyResult={},record={}", moneyResult.toString(), record.toString());
                        }
                    }
                }
                //汇总即时彩池
                summaryLivePot(gameId, tableNum, bootNum, bureauNum, newAddBetList);
            } finally {
                RedissLockUtil.unlock(livePotLockKey);
            }
        }
        //异步推送新增 投注记录
        pushGameDataToClientService.syncLivePot(gameId,tableNum,bootNum,bureauNum,newAddBetList);
        return Result.succeed(newAddBetList);
    }

    /**
     * 根据玩法获取限红范围
     *
     * @return
     */
    public Map<String, BigDecimal> getMinMaxLimitRed(GameRoomList gameRoomList, String betCode) {
        Map<String, BigDecimal> limitRed = new HashMap<>();
        //庄，闲，大，小
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


    public LivePotVo getLivePotVo(String betDataCo, String betName, BigDecimal newAddBetAmount, Integer onlineNum) {
        LivePotVo livePotVo = new LivePotVo();
        livePotVo.setBetCode(betDataCo);
        livePotVo.setBetName(betName);
        livePotVo.setBetAmount(newAddBetAmount);
        livePotVo.setOnlineNum(onlineNum);
        return livePotVo;
    }

    /**
     * 查询本局之前的下注记录
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
        GameList gameList = gameListService.getById(gameId);
        if (gameList == null) {
            return Result.failed("当前游戏不存在");
        }
        if (gameList.getGameStatus() == 0) {
            return Result.failed("当前游戏已被禁用");
        }
        if (gameList.getGameStatus() == 2) {
            return Result.failed("当前游戏正在维护");
        }
        return Result.succeed(gameList);
    }

    public Result checkTable(Long gameId, String tableNum) {
        GameRoomList gameRoomList = gameRoomListService.lambdaQuery().eq(GameRoomList::getGameId, gameId).eq(GameRoomList::getGameRoomName, tableNum).one();
        if (gameRoomList == null) {
            return Result.failed("当前桌台不存在");
        }
        if (gameRoomList.getRoomStatus() == 0) {
            return Result.failed("当前桌台已被禁用");
        }
        if (gameRoomList.getRoomStatus() == 2) {
            return Result.failed("当前桌台正在维护");
        }
        return Result.succeed(gameRoomList);
    }

    /**
     * 限红算法
     * 1.实际上庄闲（大小）就看一局中所有玩家这两种玩法的下注总额差的绝对值在限红范围就行，
     * 2.和只有他自己，所有玩家下注值累计起来在限红范围就行，
     * 3.庄对，闲对是所有玩家这两种玩法的累计下注和在限红范围就行，
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
        //获取本地设置的限红值
        Map<String, BigDecimal> minMaxLimitRed = getMinMaxLimitRed(gameRoomList, betCode);
        if (CollectionUtils.isEmpty(minMaxLimitRed)) {
            return Result.succeed();
        }
        BigDecimal minLimitRedAmount = minMaxLimitRed.get(minLimitRed);
        BigDecimal maxLimitRedAmount = minMaxLimitRed.get(maxLimitRed);
        if (minLimitRedAmount == null || maxLimitRedAmount == null) {
            return Result.succeed();
        }
        //本局累计投注额
        BigDecimal limitBetAmount = getBureauNumLimitBetAmount(gameId, tableNum, bootNum, bureauNum, betCode);
        BigDecimal betAmount = new BigDecimal(betDataCo.getBetAmount());
        BigDecimal totalBetAmount = limitBetAmount.add(betAmount);
        //大于等于最小，小于等于最大
        if (totalBetAmount.compareTo(minLimitRedAmount) > -1 && totalBetAmount.compareTo(maxLimitRedAmount) < 1) {
            return Result.succeed();
        }
        return Result.failed(betDataCo.getBetName() + "玩法下注金额超过限红值");

    }

    /**
     * 获取本局累计投注
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
        //庄，闲
        if (PlayEnum.BAC_BANKER.getCode().equals(betCode) || PlayEnum.BAC_PLAYER.getCode().equals(betCode)) {
            Object banker = redisRepository.getHashValues(redisDataKey, PlayEnum.BAC_BANKER.getCode());
            Object player = redisRepository.getHashValues(redisDataKey, PlayEnum.BAC_PLAYER.getCode());
            BigDecimal absBetAmount = getAbsBetAmount(banker, player);
            return absBetAmount;
            //大小
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
        gameRecord.setGameId(gameId.toString());
        gameRecord.setGameName(gameName);
        gameRecord.setBetCode(betDataCo.getBetCode());
        gameRecord.setBetName(betDataCo.getBetName());
        gameRecord.setBetAmount(new BigDecimal(betDataCo.getBetAmount()));
        gameRecord.setBetTime(new Date());
        gameRecord.setIp(ip);
        //注单号
        String betId = getBetId(gameId, co.getTableNum(), bootNum, bureauNum);
        gameRecord.setBetId(betId);
        //设置限红范围
        Map<String, BigDecimal> minMaxLimitRed = getMinMaxLimitRed(gameRoomList, betDataCo.getBetCode());
        if (!CollectionUtils.isEmpty(minMaxLimitRed)) {
            gameRecord.setMinLimitRed(minMaxLimitRed.get(minLimitRed));
            gameRecord.setMaxLimitRed(minMaxLimitRed.get(maxLimitRed));
        }
        return gameRecord;
    }

    /**
     * 计算即时彩池数据
     *
     * @param gameId        游戏
     * @param tableNum      桌号
     * @param bootNum       靴号
     * @param bureauNum     局数
     * @param newAddBetList 本次下注新增的数据
     */
    public void summaryLivePot(Long gameId, String tableNum, String bootNum, String bureauNum, List<LivePotVo> newAddBetList) {
        if (CollectionUtils.isEmpty(newAddBetList)) {
            return;
        }
        String groupId = gameId + "-" + tableNum + "-" + bootNum + "-" + bureauNum;
        String redisDataKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_DATA + groupId;
        for (LivePotVo vo : newAddBetList) {
            LivePotVo livePotVo = (LivePotVo) redisRepository.getHashValues(redisDataKey, vo.getBetCode());
            if (ObjectUtils.isEmpty(livePotVo)) {
                livePotVo = new LivePotVo();
                BeanUtils.copyProperties(vo, livePotVo);
            } else {
                livePotVo.setBetAmount(livePotVo.getBetAmount().add(vo.getBetAmount()));
                livePotVo.setOnlineNum(livePotVo.getOnlineNum() + vo.getOnlineNum());
            }
            redisRepository.putHashValue(redisDataKey, vo.getBetCode(), livePotVo);
            redisRepository.setExpire(redisDataKey, 60 * 60);
        }
    }

    /**
     * 注单号生成规则：游戏拼音缩写+日期+桌号+靴号+第几局+十位随机数  必须是唯一值
     *
     * @param gameId    游戏ID
     * @param tableNum  桌号
     * @param bootNum   靴号
     * @param bureauNum 局号
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
     * 列表
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
    public List<LivePotVo> getLivePot(Long gameId, String tableNum, String bootNum, String bureauNum) {
        String groupId = gameId + "-" + tableNum + "-" + bootNum + "-" + bureauNum;
        String redisDataKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_DATA + groupId;
        Map<String, Object> totalBet = redisRepository.getHashValue(redisDataKey);
        List<LivePotVo> list = new ArrayList<>();
        //所有玩法
        List<PlayEnum> playList = PlayEnum.getPlayListByGameId(gameId);
        for (PlayEnum playEnum : playList) {
            LivePotVo livePotVo = (LivePotVo) totalBet.get(playEnum.getCode());
            if (livePotVo == null) {
                livePotVo = new LivePotVo();
                livePotVo.setBetCode(playEnum.getCode());
                livePotVo.setBetName(playEnum.getName());
                livePotVo.setBetAmount(BigDecimal.ZERO);
            }
            list.add(livePotVo);
        }
        return list;
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
        return PageResult.<GameRecordVo>builder().data(list).count(page.getTotal()).build();
    }

    @Override
    public List<GameRecord> getGameRecordByBureauNum(Long gameId, String tableNum, String bootNum, String bureauNum) {
        LambdaQueryWrapper<GameRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameRecord::getGameId, gameId);
        lqw.eq(GameRecord::getTableNum, tableNum);
        lqw.eq(GameRecord::getBootNum, bootNum);
        lqw.eq(GameRecord::getBureauNum, bureauNum);
        List<GameRecord> gameRecords = gameRecordMapper.selectList(lqw);
        return gameRecords;
    }

    @Override
    public List<PayoutResultVo> getPayoutResult(String gameId, String tableNum, String bootNum, String bureauNum) {
        return gameRecordMapper.getPayoutResult(gameId, tableNum, bootNum, bureauNum);

    }

    @Override
    public List<RankingListVo> getTodayLotteryList() {
        String startTime = DateUtil.getStartTime(0);
        String endTime = DateUtil.getEndTime(0);
        return gameRecordMapper.getTodayLotteryList(startTime, endTime);
    }

    @Override
    public List<RankingListVo> getTodayBetList() {
        String startTime = DateUtil.getStartTime(0);
        String endTime = DateUtil.getEndTime(0);
        return gameRecordMapper.getTodayBetList(startTime, endTime);
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
            //总局数
            int totalNum = totalList.size();
            //盈利的
            List<BigDecimal> winList = totalList.stream().filter(t -> t.compareTo(BigDecimal.ZERO) == 1).collect(Collectors.toList());
            int winNum = winList.size();
            //胜率
            BigDecimal rate = new BigDecimal(winNum).divide(new BigDecimal(totalNum)).multiply(new BigDecimal(100));
            vo.setRete(keepDecimal(rate).toString());
        }
        return list;
    }

    private BigDecimal keepDecimal(BigDecimal val) {
        return val == null ? BigDecimal.ZERO.setScale(2) : val.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
