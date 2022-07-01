package com.central.game.constants;

public enum GameRoomInfoOfflineStatusEnum {

    SHUFFLING(0, "洗牌中"),
    START_BETTING(1, "开始下注"),
    END_BETTING(2, "停止下注"),
    SETTLEMENT_END(4, "结算完成");

    private Integer status;
    private String name;

    GameRoomInfoOfflineStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }
}
