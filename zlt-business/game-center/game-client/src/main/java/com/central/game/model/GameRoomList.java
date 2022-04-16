package com.central.game.model;

import com.baomidou.mybatisplus.annotation.*;
import com.central.common.model.SuperEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@TableName("game_room_list")
@ApiModel("桌号列表")
public class GameRoomList extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "游戏表id")
    private Long gameId;

    @ApiModelProperty(value = "游戏桌号名称")
    private String gameRoomName;

    @ApiModelProperty(value = "游戏桌号状态 0禁用，1：正常，2：维护")
    private Integer roomStatus;

    @ApiModelProperty(value = "返佣类型1：免佣，2：抽佣")
    private Integer commissionType;

    @ApiModelProperty(value = "庄闲最小限红")
    private BigDecimal minBankerPlayer;

    @ApiModelProperty(value = "庄闲最大限红")
    private BigDecimal maxBankerPlayer;

    @ApiModelProperty(value = "和最小限红")
    private BigDecimal minSum;

    @ApiModelProperty(value = "和最大限红")
    private BigDecimal maxSum;

    @ApiModelProperty(value = "对子最小限红")
    private BigDecimal minTwain;

    @ApiModelProperty(value = "对子最大限红")
    private BigDecimal maxTwain;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "维护开始时间")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date maintainStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "维护结束时间")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date maintainEnd;
/*
    @ApiModelProperty(value = "牌局状态 0洗牌中 1开始下注 2停止下注 3结算中 4结算完成")
    @TableField(exist = false)
    private Integer status;*/
}
