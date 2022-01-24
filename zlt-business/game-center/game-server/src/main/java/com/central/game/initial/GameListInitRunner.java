package com.central.game.initial;

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

    @Autowired
    private IGameListService gameListService;

    @Override
    public void run(String... args) throws Exception {
        log.info("启动项目游戏列表");
        BigDecimal gameRate = new BigDecimal("0");
        saveGameList(1L, "百家乐", gameRate);
        saveGameList(2L, "龙虎", gameRate);
        saveGameList(3L, "色碟", gameRate);
        saveGameList(4L, "轮盘", gameRate);
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
