package com.central.game.constants;

import java.math.BigDecimal;

public enum PlayEnum {

    BAC_BANKER("4", "庄", new BigDecimal("0.95")),
    BAC_PLAYER("1", "闲", new BigDecimal("1")),
    BAC_BIG("9", "大", new BigDecimal("0.5")),
    BAC_SMALL("6", "小", new BigDecimal("1.5")),
    BAC_TIE("7", "和", new BigDecimal("8")),
    BAC_BPAIR("8", "庄对", new BigDecimal("11")),
    BAC_PPAIR("5", "闲对", new BigDecimal("11"));

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

    PlayEnum(String code, String name, BigDecimal odds) {
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

}
