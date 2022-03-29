package com.central.game.constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public enum PlayEnum {

    BAC_BANKER(GameListEnum.BACCARAT.getGameId(), "4", "庄", new BigDecimal("0.95")),
    BAC_PLAYER(GameListEnum.BACCARAT.getGameId(), "1", "闲", new BigDecimal("1")),
    BAC_BIG(GameListEnum.BACCARAT.getGameId(), "9", "大", new BigDecimal("0.5")),
    BAC_SMALL(GameListEnum.BACCARAT.getGameId(), "6", "小", new BigDecimal("1.5")),
    BAC_TIE(GameListEnum.BACCARAT.getGameId(), "7", "和", new BigDecimal("8")),
    BAC_BPAIR(GameListEnum.BACCARAT.getGameId(), "8", "庄对", new BigDecimal("11")),
    BAC_PPAIR(GameListEnum.BACCARAT.getGameId(), "5", "闲对", new BigDecimal("11"));

    private Long gameId;

    private String code;

    private String name;

    /**
     * 赔率
     */
    private BigDecimal odds;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getOdds() {
        return odds;
    }

    PlayEnum(Long gameId, String code, String name, BigDecimal odds) {
        this.gameId = gameId;
        this.code = code;
        this.name = name;
        this.odds = odds;
    }

    public static PlayEnum getPlayByCode(String code) {
        for (PlayEnum playEnum : PlayEnum.values()) {
            if (playEnum.code.equals(code)) {
                return playEnum;
            }
        }
        return null;
    }

    public static List<PlayEnum> getPlayListByGameId(Long gameId) {
        List<PlayEnum> list = new ArrayList<>();
        for (PlayEnum playEnum : PlayEnum.values()) {
            if (playEnum.gameId.equals(gameId)) {
                list.add(playEnum);
            }
        }
        return list;
    }

}
