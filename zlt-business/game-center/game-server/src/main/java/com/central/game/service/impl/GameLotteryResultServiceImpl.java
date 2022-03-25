package com.central.game.service.impl;

import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.mapper.GameLotteryResultMapper;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.game.service.IGameLotteryResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@Service
public class GameLotteryResultServiceImpl extends SuperServiceImpl<GameLotteryResultMapper, GameLotteryResult> implements IGameLotteryResultService {

    @Autowired
    private GameLotteryResultMapper gameLotteryResultMapper;

    @Override
    public List<GameLotteryResult> getLotteryResultList(GameLotteryResultCo co) {
        return gameLotteryResultMapper.getLotteryResultList(co);
    }
}
