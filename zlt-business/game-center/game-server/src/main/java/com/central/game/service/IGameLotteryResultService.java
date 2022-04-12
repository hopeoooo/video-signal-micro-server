package com.central.game.service;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.service.ISuperService;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameLotteryResultBackstageCo;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.vo.GameRoomListVo;
import com.central.game.model.vo.LivePotVo;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.validation.Valid;
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

    PageResult<GameLotteryResult> findList(GameLotteryResultBackstageCo map);

    void setLotteryNum(List<GameLotteryResult> lotteryResultList, GameRoomListVo vo);

    List<GameLotteryResult> getBootNumResultList(Long gameId, String tableNum, String bootNum);
}

