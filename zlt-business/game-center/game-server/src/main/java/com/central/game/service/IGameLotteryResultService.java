package com.central.game.service;

import com.central.common.service.ISuperService;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.co.GameLotteryResultCo;

import java.util.List;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
public interface IGameLotteryResultService extends ISuperService<GameLotteryResult> {

    List<GameLotteryResult> getLotteryResultList(GameLotteryResultCo co);

    /**
     * 计算下注结果
     * @param result
     */
    void calculateBetResult(GameLotteryResult result);


}

