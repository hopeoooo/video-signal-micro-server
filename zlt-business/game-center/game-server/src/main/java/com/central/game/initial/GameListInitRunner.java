package com.central.game.initial;

import com.central.game.constants.GameListEnum;
import com.central.game.model.GameList;
import com.central.game.service.IGameListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 启动项目初始化游戏列表
 */
@Component
@Slf4j
@Order(1)
public class GameListInitRunner implements CommandLineRunner {

    public static final Long BACCARAT = 1L;
    public static final Long DRAGON_TIGER = 2L;
    public static final Long SE_DIE = 3L;
    public static final Long ROULETTE = 4L;

    @Autowired
    private IGameListService gameListService;

    @Override
    public void run(String... args) throws Exception {
        log.info("启动项目游戏列表");
        BigDecimal gameRate = BigDecimal.ZERO;
        saveGameList(GameListEnum.BACCARAT.getGameId(), GameListEnum.BACCARAT.getGameName(), gameRate);
        saveGameList(GameListEnum.DRAGON_TIGER.getGameId(), GameListEnum.DRAGON_TIGER.getGameName(), gameRate);
        saveGameList(GameListEnum.SE_DIE.getGameId(), GameListEnum.SE_DIE.getGameName(), gameRate);
        saveGameList(GameListEnum.ROULETTE.getGameId(), GameListEnum.ROULETTE.getGameName(), gameRate);
        log.info("游戏列表初始化完成");
    }

    public void saveGameList(Long id, String gameName, BigDecimal gameRate) {
        GameList gameList = gameListService.getById(id);
        if (gameList != null) {
            return;
        }
        gameList = new GameList();
        gameList.setId(id);
        gameList.setName(gameName);
        gameList.setGameRate(gameRate);
        gameList.setGameStatus(1);
        gameList.setRateStatus(1);
        gameListService.save(gameList);
    }
}
