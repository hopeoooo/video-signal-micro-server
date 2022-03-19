package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.constants.GameListEnum;
import com.central.game.initial.GameListInitRunner;
import com.central.game.mapper.GameRecordMapper;
import com.central.game.model.GameList;
import com.central.game.model.GameRecord;
import com.central.game.model.GameRoomList;
import com.central.game.model.co.GameRecordBetDataCo;
import com.central.game.model.co.GameRecordCo;
import com.central.game.service.IGameListService;
import com.central.game.service.IGameRecordService;
import com.central.game.service.IGameRoomListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
            BigDecimal betAmount = new BigDecimal(betDataCo.getBetAmount());
            if (betAmount.compareTo(BigDecimal.ZERO) < 1) {
                return Result.failed(betDataCo.getBetName() + "下注金额必须大于0");
            }
        }
        //先删除之前用户本局保存的注单数据
        LambdaQueryWrapper<GameRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameRecord::getUserId, user.getId());
        lqw.eq(GameRecord::getGameId, gameId);
        lqw.eq(GameRecord::getTableNum, co.getTableNum());
        lqw.eq(GameRecord::getBootNum, co.getBootNum());
        lqw.eq(GameRecord::getBureauNum, co.getBureauNum());
        gameRecordMapper.delete(lqw);
        //再新增
        GameRecord gameRecord = null;
        for (GameRecordBetDataCo betDataCo : betResult) {
            gameRecord = new GameRecord();
            gameRecord.setTableNum(co.getTableNum());
            gameRecord.setBootNum(co.getBootNum());
            gameRecord.setBureauNum(co.getBureauNum());
            gameRecord.setUserId(user.getId());
            gameRecord.setUserName(user.getUsername());
            gameRecord.setGameId(gameId.toString());
            gameRecord.setGameName(gameList.getName());
            gameRecord.setBetCode(betDataCo.getBetCode());
            gameRecord.setBetName(betDataCo.getBetName());
            gameRecord.setBetAmount(new BigDecimal(betDataCo.getBetAmount()));
            gameRecord.setBetTime(new Date());
            gameRecord.setIp(ip);
            //注单号
            String betId = getBetId(gameId, co.getTableNum(), co.getBootNum(), co.getBureauNum());
            gameRecord.setBetId(betId);
            gameRecordMapper.insert(gameRecord);
        }
        return Result.succeed();
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
}
