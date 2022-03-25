package com.central.game.service;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.service.ISuperService;
import com.central.game.dto.GameRecordDto;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRecordBetTotalCo;
import com.central.game.model.co.GameRecordCo;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
public interface IGameLotteryResultService extends ISuperService<GameLotteryResult> {

    List<GameLotteryResult> getLotteryResultList(GameLotteryResultCo co);
}

