package com.central.game.service;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.service.ISuperService;
import com.central.game.dto.GameRecordDto;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRecordCo;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
public interface IGameRecordService extends ISuperService<GameRecord> {

    PageResult<GameRecord> findList(GameRecordBetCo map);


    GameRecordDto findGameRecordTotal(GameRecordBetCo params);

    Result saveRecord(GameRecordCo co, SysUser user, String ip);

    GameRecordReportDto findBetAmountTotal(Map<String, Object> params);

    GameRecordReportDto findValidbetTotal(Map<String, Object> params);

    GameRecordReportDto findWinningAmountTotal(Map<String, Object> params);

    List<GameRecord> getGameRecordByParent(GameRecordBetCo params);
}

