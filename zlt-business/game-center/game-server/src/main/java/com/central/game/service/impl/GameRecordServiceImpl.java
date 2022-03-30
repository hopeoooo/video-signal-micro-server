package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.*;
import com.central.common.redis.constant.RedisKeyConstant;
import com.central.common.redis.lock.RedissLockUtil;
import com.central.common.redis.template.RedisRepository;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.constants.GameListEnum;
import com.central.game.constants.PlayEnum;
import com.central.game.dto.GameRecordDto;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.dto.HomeHistogramDto;
import com.central.game.dto.HomePageDto;
import com.central.game.mapper.GameRecordMapper;
import com.central.game.model.GameList;
import com.central.game.model.GameRecord;
import com.central.game.model.GameRoomList;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRecordBetDataCo;
import com.central.game.model.co.GameRecordCo;
import com.central.game.model.vo.LivePotVo;
import com.central.game.service.IGameListService;
import com.central.game.service.IGameRecordService;
import com.central.game.service.IGameRoomListService;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import com.central.user.feign.UserService;
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

    @Override
    @Transactional
    public Result<List<LivePotVo>> saveRecord(GameRecordCo co, SysUser user, String ip) {
        String tableNum = co.getTableNum();
        GameRoomList gameRoomList = gameRoomListService.findById(tableNum);
        if (gameRoomList == null) {
            return Result.failed("当前桌台不存在");
        }
        if (gameRoomList.getRoomStatus() == 0) {
            return Result.failed("当前桌台已被禁用");
        }
        if (gameRoomList.getRoomStatus() == 2) {
            return Result.failed("当前桌台正在维护");
        }
        Long gameId = gameRoomList.getGameId();
        GameList gameList = gameListService.getById(gameId);
        if (gameList.getGameStatus() == 0) {
            return Result.failed("当前游戏已被禁用");
        }
        if (gameList.getGameStatus() == 2) {
            return Result.failed("当前游戏正在维护");
        }
        List<GameRecordBetDataCo> betResult = co.getBetResult();
        for (GameRecordBetDataCo betDataCo : betResult) {
            PlayEnum playEnum = PlayEnum.getPlayByCode(betDataCo.getBetCode());
            if (playEnum == null) {
                return Result.failed(betDataCo.getBetName() + "玩法不支持");
            }
            BigDecimal betAmount = new BigDecimal(betDataCo.getBetAmount());
            if (betAmount.compareTo(BigDecimal.ZERO) < 1) {
                return Result.failed(betDataCo.getBetName() + "下注金额必须大于0");
            }
        }
        //先查询上次用户本局保存的注单数据
        LambdaQueryWrapper<GameRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameRecord::getUserId, user.getId());
        lqw.eq(GameRecord::getGameId, gameId);
        lqw.eq(GameRecord::getTableNum, co.getTableNum());
        lqw.eq(GameRecord::getBootNum, co.getBootNum());
        lqw.eq(GameRecord::getBureauNum, co.getBureauNum());
        List<GameRecord> gameRecords = gameRecordMapper.selectList(lqw);
        //本次下注下注数据
        List<LivePotVo> newAddBetList = new ArrayList<>();
        //同一种玩法投注额变化时更新
        for (GameRecordBetDataCo betDataCo : betResult) {
            boolean flag = false;
            for (GameRecord record : gameRecords) {
                BigDecimal newBetAmount = new BigDecimal(betDataCo.getBetAmount());
                if (record.getGameId().equals(gameId.toString()) && record.getBetCode().equals(betDataCo.getBetCode())) {
                    //与之前投注额相等不更新
                    if (newBetAmount.compareTo(record.getBetAmount()) == 0) {
                        flag = true;
                        break;
                    }
                    //新旧投注额差
                    BigDecimal diffBetAmount = newBetAmount.subtract(record.getBetAmount());
                    record.setBetAmount(newBetAmount);
                    //扣减本地余额
                    Result<SysUserMoney> moneyResult = userService.transterMoney(user.getId(), diffBetAmount, null, CapitalEnum.BET.getType(), null, record.getBetId());
                    //本地余额扣减成功，更新投注记录
                    if (moneyResult.getResp_code() == CodeEnum.SUCCESS.getCode()) {
                        gameRecordMapper.updateById(record);
                    } else {
                        log.error("投注时本地余额扣减失败，moneyResult={},record={}", moneyResult.toString(), record.toString());
                    }
                    //汇总新增的下注额
                    newAddBetList.add(getLivePotVo(betDataCo.getBetCode(), betDataCo.getBetName(), diffBetAmount));
                    flag = true;
                    break;
                }
            }
            //之前没有保存的新增
            if (!flag) {
                //先扣减本地余额
                GameRecord record = getGameRecord(co, betDataCo, user, gameId, gameList.getName(), ip);
                Result<SysUserMoney> moneyResult = userService.transterMoney(user.getId(), record.getBetAmount(), null, CapitalEnum.BET.getType(), null, record.getBetId());
                //本地余额扣减成功，保存投注记录
                if (moneyResult.getResp_code() == CodeEnum.SUCCESS.getCode()) {
                    gameRecordMapper.insert(record);
                } else {
                    log.error("投注时本地余额扣减失败，moneyResult={},record={}", moneyResult.toString(), record.toString());
                }
                //汇总新增的下注额
                newAddBetList.add(getLivePotVo(betDataCo.getBetCode(), betDataCo.getBetName(), record.getBetAmount()));
            }
        }
        return Result.succeed(newAddBetList);
    }

    public LivePotVo getLivePotVo(String betDataCo, String betName, BigDecimal newAddBetAmount) {
        LivePotVo livePotVo = new LivePotVo();
        livePotVo.setBetCode(betDataCo);
        livePotVo.setBetName(betName);
        livePotVo.setBetAmount(newAddBetAmount);
        return livePotVo;
    }

    public GameRecord getGameRecord(GameRecordCo co, GameRecordBetDataCo betDataCo, SysUser user, Long gameId, String gameName, String ip) {
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
        String groupId = "livePot-" + gameId + "-" + tableNum + "-" + bootNum + "-" + bureauNum;
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
                    }
                    redisRepository.putHashValue(redisDataKey, vo.getBetCode(), livePotVo);
                    redisRepository.setExpire(redisDataKey, 60 * 60);
                }
            } finally {
                RedissLockUtil.unlock(livePotLockKey);
            }
        }
        List<LivePotVo> livePot = getLivePot(gameId, tableNum, bootNum, bureauNum);
        if (CollectionUtils.isEmpty(livePot)) {
            return;
        }
        PushResult<List<LivePotVo>> pushResult = PushResult.succeed(livePot, SocketTypeConstant.LIVE_POT, "即时彩池数据送成功");
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
    public PageResult<GameRecord> findList(GameRecordBetCo params) {
        Page<GameRecord> page = new Page<>(params.getPage(), params.getLimit());
        List<GameRecord> list = baseMapper.findGameRecordList(page, params);
        return PageResult.<GameRecord>builder().data(list).count(page.getTotal()).build();
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
        if (!CollectionUtils.isEmpty(totalBet)) {
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
        }
        return list;
    }

    @Override
    public HomeHistogramDto findHomeHistogramDto(Map<String, Object> params) {
        return gameRecordMapper.findHomeHistogramDto(params);
    }
}
