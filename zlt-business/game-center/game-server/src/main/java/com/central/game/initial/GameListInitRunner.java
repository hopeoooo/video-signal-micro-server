package com.central.game.initial;

import com.central.common.constant.CommonConstant;
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
        for (GameListEnum game : GameListEnum.values()) {
            saveGameList(game);
        }
        log.info("游戏列表初始化完成");
    }

    public void saveGameList(GameListEnum gameListEnum) {
        GameList gameList = gameListService.getById(gameListEnum.getGameId());
        if (gameList != null) {
            return;
        }
        gameList = new GameList();
        gameList.setId(gameListEnum.getGameId());
        gameList.setName(gameListEnum.getGameName());
//        gameList.setEnName(gameListEnum.getGameEnName());
        gameList.setGameRate(BigDecimal.ZERO);
        gameList.setGameStatus(CommonConstant.OPEN);
        gameList.setRateStatus(CommonConstant.OPEN);
        gameListService.save(gameList);
    }
}
