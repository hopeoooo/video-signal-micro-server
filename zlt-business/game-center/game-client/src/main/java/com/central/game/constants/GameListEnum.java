package com.central.game.constants;

import lombok.Data;

public enum GameListEnum {
    BACCARAT(1L, "百家乐", "BAC"),
    DRAGON_TIGER(2L, "龙虎", "DGTG"),
    SE_DIE(3L, "色碟", "SEDIE"),
    ROULETTE(4L, "百家乐", "ROU"),
    ;

    private Long gameId;

    private String gameName;

    //简称
    private String abbreviation;

    GameListEnum(Long gameId, String gameName, String abbreviation) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.abbreviation = abbreviation;
    }

    public Long getGameId() {
        return gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static String getAbbreviationByGameId(Long gameId) {
        for (GameListEnum game : GameListEnum.values()) {
            if (game.gameId == gameId) {
                return game.abbreviation;
            }
        }
        return null;
    }


}
