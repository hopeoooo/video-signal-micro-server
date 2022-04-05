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
import com.central.game.model.co.GameRecordBetPageCo;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRecordBetDataCo;
import com.central.game.model.co.GameRecordCo;
import com.central.game.model.vo.GameRecordVo;
import com.central.game.model.vo.GameRecordBackstageVo;
import com.central.game.model.vo.LivePotVo;
import com.central.game.service.IGameListService;
import com.central.game.service.IGameRecordService;
import com.central.game.service.IGameRoomInfoOfflineService;
import com.central.game.service.IGameRoomListService;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import com.central.user.feign.UserService;
import com.central.user.model.vo.SysUserMoneyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private PushService pushService;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private IGameRoomInfoOfflineService gameRoomInfoOfflineService;

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
        GameRoomInfoOffline infoOffline = gameRoomInfoOfflineService.findByGameIdAndTableNumAndBootNumAndBureauNum(co.getGameId().toString(), co.getTableNum(), co.getBootNum(), co.getBureauNum());
        if (infoOffline == null || infoOffline.getStatus() != 1) {
            return Result.failed("已停止下注");
        }
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
        //先查询上次用户本局保存的注单数据
        List<GameRecord> gameRecords = getBeforeBureauNum(user.getId(), gameId, co.getTableNum(), co.getBootNum(), co.getBureauNum());
        //本次下注成功的数据
        List<LivePotVo> newAddBetList = new ArrayList<>();
        //同一种玩法投注额变化时更新
        for (GameRecordBetDataCo betDataCo : betResult) {
            boolean flag = false;
            for (GameRecord record : gameRecords) {
                BigDecimal newBetAmount = new BigDecimal(betDataCo.getBetAmount());
                if (record.getGameId().equals(gameId.toString()) && record.getBetCode().equals(betDataCo.getBetCode())) {
                    record.setBetAmount(record.getBetAmount().add(newBetAmount));
                    //设置限红范围
                    List<BigDecimal> minMaxLimitRed = getMinMaxLimitRed(gameRoomList, betDataCo.getBetCode());
                    if (!CollectionUtils.isEmpty(minMaxLimitRed)) {
                        record.setMinLimitRed(minMaxLimitRed.get(0));
                        record.setMaxLimitRed(minMaxLimitRed.get(1));
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
                GameRecord record = getGameRecord(co, gameRoomList, betDataCo, user, gameId, gameList.getName(), ip);
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
        return Result.succeed(newAddBetList);
    }

    /**
     * 根据玩法获取限红范围
     *
     * @return
     */
    public List<BigDecimal> getMinMaxLimitRed(GameRoomList gameRoomList, String betCode) {
        List<BigDecimal> list = new ArrayList<>();
        //庄，闲，大，小
        if (betCode == PlayEnum.BAC_BANKER.getCode() || betCode == PlayEnum.BAC_PLAYER.getCode() || betCode == PlayEnum.BAC_BIG.getCode() || betCode == PlayEnum.BAC_SMALL.getCode()) {
            list.add(gameRoomList.getMinBankerPlayer());
            list.add(gameRoomList.getMaxBankerPlayer());
        } else if (betCode == PlayEnum.BAC_TIE.getCode()) {
            list.add(gameRoomList.getMinSum());
            list.add(gameRoomList.getMinSum());
        } else if (betCode == PlayEnum.BAC_BPAIR.getCode() || betCode == PlayEnum.BAC_PPAIR.getCode()) {
            list.add(gameRoomList.getMinTwain());
            list.add(gameRoomList.getMaxTwain());
        }
        return list;
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

    public GameRecord getGameRecord(GameRecordCo co, GameRoomList gameRoomList, GameRecordBetDataCo betDataCo, SysUser user, Long gameId, String gameName, String ip) {
        GameRecord gameRecord = new GameRecord();
        gameRecord.setTableNum(co.getTableNum());
        gameRecord.setBootNum(co.getBootNum());
        gameRecord.setBureauNum(co.getBureauNum());
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
        String betId = getBetId(gameId, co.getTableNum(), co.getBootNum(), co.getBureauNum());
        gameRecord.setBetId(betId);
        //设置限红范围
        List<BigDecimal> minMaxLimitRed = getMinMaxLimitRed(gameRoomList, betDataCo.getBetCode());
        if (!CollectionUtils.isEmpty(minMaxLimitRed)) {
            gameRecord.setMinLimitRed(minMaxLimitRed.get(0));
            gameRecord.setMaxLimitRed(minMaxLimitRed.get(1));
        }
        return gameRecord;
    }

    /**
     * 下注完毕异步汇总本局即时彩池数据
     *
     * @param gameId        游戏
     * @param tableNum      桌号
     * @param bootNum       靴号
     * @param bureauNum     局数
     * @param newAddBetList 本次下注新增的数据
     */
    @Override
    @Async
    public void syncLivePot(Long gameId, String tableNum, String bootNum, String bureauNum, List<LivePotVo> newAddBetList) {
        if (CollectionUtils.isEmpty(newAddBetList)) {
            return;
        }
        String groupId = gameId + "-" + tableNum + "-" + bootNum + "-" + bureauNum;
        String livePotLockKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_LOCK + groupId;
        String redisDataKey = RedisKeyConstant.GAME_RECORD_LIVE_POT_DATA + groupId;
        boolean livePotLock = RedissLockUtil.tryLock(livePotLockKey, RedisKeyConstant.WAIT_TIME, RedisKeyConstant.LEASE_TIME);
        if (livePotLock) {
            try {
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
            } finally {
                RedissLockUtil.unlock(livePotLockKey);
            }
        }
        PushResult<List<LivePotVo>> pushResult = PushResult.succeed(newAddBetList, SocketTypeConstant.LIVE_POT, "即时彩池数据送成功");
        Result<String> push = pushService.sendMessageByGroupId(groupId, com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("即时彩池数据推送结果:groupId={},result={}", groupId, push);
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
}
