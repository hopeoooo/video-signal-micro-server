package com.central.game.initial;

import com.central.game.model.GamePlayConfig;
import com.central.game.service.IGamePlayConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动项目初始化游戏玩法配置
 */
@Component
@Slf4j
@Order(2)
public class GamePlayConfigInitRunner{

    @Autowired
    private IGamePlayConfigService gamePlayConfigService;

//    @Override
    public void run(String... args) throws Exception {
        log.info("启动项目初始化游戏玩法配置");
        saveGamePlayConfig(GameListInitRunner.BACCARAT, "庄", "1:0.95", 0);
        saveGamePlayConfig(GameListInitRunner.BACCARAT, "闲", "1:1", 0);
        saveGamePlayConfig(GameListInitRunner.BACCARAT, "大", "1:0.5", 30);
        saveGamePlayConfig(GameListInitRunner.BACCARAT, "小", "1:1.5", 30);
        saveGamePlayConfig(GameListInitRunner.BACCARAT, "和", "1:8", 0);
        saveGamePlayConfig(GameListInitRunner.BACCARAT, "庄对", "1:11", 0);
        saveGamePlayConfig(GameListInitRunner.BACCARAT, "闲对", "1:11", 0);
        log.info("游戏玩法配置初始化完成");
    }

    public void saveGamePlayConfig(Long gameId, String playName, String odds, Integer bureauLimit) {
        GamePlayConfig playConfig = gamePlayConfigService.lambdaQuery().eq(GamePlayConfig::getGameId, gameId).eq(GamePlayConfig::getPlayName, playName).one();
        if (playConfig != null) {
            return;
        }
        playConfig = new GamePlayConfig();
        playConfig.setGameId(gameId);
        playConfig.setPlayName(playName);
        playConfig.setOdds(odds);
        playConfig.setBureauLimit(bureauLimit);
        playConfig.setStatus(1);
        gamePlayConfigService.save(playConfig);
    }
}
