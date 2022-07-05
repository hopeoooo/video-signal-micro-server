package com.central.game.constants;


public enum GameListEnum {
    BACCARAT(1L, "百家乐", "Baccarat","BAC"),
    DRAGON_TIGER(2L, "龙虎", "Dragon Tiger","DGTG"),
    SE_DIE(3L, "色碟", "Se Die","SEDIE"),
    ROULETTE(4L, "轮盘", "Roulette","ROU"),
    ;

    private Long gameId;

    private String gameName;

    private String gameEnName;

    //简称
    private String abbreviation;

    GameListEnum(Long gameId, String gameName,String gameEnName, String abbreviation) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.gameEnName = gameEnName;
        this.abbreviation = abbreviation;
    }

    public Long getGameId() {
        return gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public String getGameEnName() {
        return gameEnName;
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
