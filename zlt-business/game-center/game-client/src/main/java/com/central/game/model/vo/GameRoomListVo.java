package com.central.game.model.vo;

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
@ApiModel("桌号列表")
public class GameRoomListVo{

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(value = "游戏表id")
    private Long gameId;

    @ApiModelProperty(value = "游戏名称")
    private String gameName;

    @ApiModelProperty(value = "游戏桌号名称")
    private String gameRoomName;

    @ApiModelProperty(value = "游戏桌号状态 0禁用，1：正常，2：维护")
    private Integer roomStatus;

    @ApiModelProperty(value = "机器编号")
    private String machineCode;

    @ApiModelProperty(value = "桌台编号")
    private String tableNum;

    @ApiModelProperty(value = "靴号")
    private String bootNum;

    @ApiModelProperty(value = "局号")
    private String bureauNum;

    @ApiModelProperty(value = "倒计时")
    private Integer second;

    @ApiModelProperty(value = "实时倒计时")
    @TableField(exist = false)
    private Integer currentSecond = 0;

    @ApiModelProperty(value = "牌局状态 0洗牌中 1开始下注 2停止下注 3结算中 4结算完成")
    private Integer status;

    @ApiModelProperty(value = "庄下注人数")
    private Integer baccaratBetNum = 0;

    @ApiModelProperty(value = "庄累计下注金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal baccaratBetAmonut = BigDecimal.ZERO;

    @ApiModelProperty(value = "闲下注人数")
    private Integer playerBetNum = 0;

    @ApiModelProperty(value = "闲累计下注金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal playerBetAmonut = BigDecimal.ZERO;

    @ApiModelProperty(value = "本局累计下注人数")
    private Integer totalBetNum = 0;

    @ApiModelProperty(value = "本局累计下注金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalBetAmonut = BigDecimal.ZERO;

    @ApiModelProperty(value = "本靴总局数")
    private Integer bootNumTotalNum = 0;

    @ApiModelProperty(value = "本靴开出庄的次数")
    private Integer bootNumBankerNum = 0;

    @ApiModelProperty(value = "本靴开出闲的次数")
    private Integer bootNumPlayerNum = 0;

    @ApiModelProperty(value = "本靴开出和的次数")
    private Integer bootNumTieNum = 0;
}
