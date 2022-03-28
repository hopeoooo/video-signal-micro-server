package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.constant.UserConstant;
import com.central.common.model.*;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.constants.GameListEnum;
import com.central.game.constants.PlayEnum;
import com.central.game.dto.GameRecordDto;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.mapper.GameRecordMapper;
import com.central.game.model.GameList;
import com.central.game.model.GameRecord;
import com.central.game.model.GameRoomList;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRecordBetDataCo;
import com.central.game.model.co.GameRecordCo;
import com.central.game.service.IGameListService;
import com.central.game.service.IGameRecordService;
import com.central.game.service.IGameRoomListService;
import com.central.user.feign.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    @Override
    @Transactional
    public Result saveRecord(GameRecordCo co, SysUser user, String ip) {
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
        //同一种玩法投注额变化时更新
        for (GameRecordBetDataCo betDataCo : betResult) {
            boolean flag = false;
            for (GameRecord record : gameRecords) {
                BigDecimal newBetAmount = new BigDecimal(betDataCo.getBetAmount());
                if (record.getGameId().equals(gameId) && record.getBetCode().equals(betDataCo.getBetCode())) {
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
            }
        }
        return Result.succeed();
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
     * @param tableNum 桌号
     * @param bootNum 靴号
     * @param bureauNum 局数
     */
    public void syncSummaryLivePot(String tableNum,String bootNum,String bureauNum){
//        String moneyKey = UserConstant.redisKey.SYS_USER_MONEY_MONEY_LOCK + userId;
//        boolean moneyLock = RedissLockUtil.tryLock(moneyKey, UserConstant.redisKey.WAIT_TIME, UserConstant.redisKey.LEASE_TIME);
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
}
