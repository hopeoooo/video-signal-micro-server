package com.central.game.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.central.common.model.SuperEntity;
import com.central.game.model.GameLotteryResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ApiModel("游戏桌号列表")
public class GameRoomListVo{

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(value = "房间id")
    private Long roomId;

    @ApiModelProperty(value = "游戏表id")
    private Long gameId;

    @ApiModelProperty(value = "游戏桌号名称")
    private String gameRoomName;

    @ApiModelProperty(value = "桌台英文名称",hidden = true)
    @JsonIgnore
    private String enName;

    @ApiModelProperty(value = "桌台高棉语名称",hidden = true)
    @JsonIgnore
    private String khmName;

    @ApiModelProperty(value = "桌台泰语名称",hidden = true)
    @JsonIgnore
    private String thName;

    @ApiModelProperty(value = "游戏桌台状态 0禁用，1：正常，2：维护")
    private Integer roomStatus;

    @ApiModelProperty(value = "桌台关注状态 0未关注，1：已关注")
    private Integer followStatus = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "维护开始时间")
    private Date maintainStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "维护结束时间")
    private Date maintainEnd;

    @ApiModelProperty(value = "游戏状态 0禁用，1：正常，2：维护")
    private Integer gameStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "游戏维护开始时间")
    private Date gameMaintainStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "游戏维护结束时间")
    private Date gameMaintainEnd;

    @ApiModelProperty(value = "国际化桌台编号")
    private String i18nTableNum;

    @ApiModelProperty(value = "桌台编号")
    private String tableNum;

    @ApiModelProperty(value = "靴号")
    private String bootNum;

    @ApiModelProperty(value = "局号")
    private String bureauNum;

    @ApiModelProperty(value = "倒计时")
    private Integer second;

    @ApiModelProperty(value = "实时倒计时")
    private Integer currentSecond = 0;

    @ApiModelProperty(value = "牌局状态 0洗牌中 1开始下注 2停止下注 4结算完成")
    private Integer status;

    @ApiModelProperty(value = "庄下注人数")
    private Integer baccaratBetNum = 0;

    @ApiModelProperty(value = "庄累计下注金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal baccaratBetAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "闲下注人数")
    private Integer playerBetNum = 0;

    @ApiModelProperty(value = "闲累计下注金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal playerBetAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "本局累计下注人数")
    private Integer totalBetNum = 0;

    @ApiModelProperty(value = "本局累计下注金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalBetAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "本靴总局数")
    private Integer bootNumTotalNum = 0;

    @ApiModelProperty(value = "本靴开出庄的次数")
    private Integer bootNumBankerNum = 0;

    @ApiModelProperty(value = "本靴开出闲的次数")
    private Integer bootNumPlayerNum = 0;

    @ApiModelProperty(value = "本靴开出和的次数")
    private Integer bootNumTieNum = 0;

    @ApiModelProperty(value = "本靴开出庄对的次数")
    private Integer bootNumBpairNum = 0;

    @ApiModelProperty(value = "本靴开出闲对的次数")
    private Integer bootNumPpairNum = 0;

    @ApiModelProperty(value = "本靴赛果明细")
    private List<GameLotteryResult>  bootNumResultList;
}
