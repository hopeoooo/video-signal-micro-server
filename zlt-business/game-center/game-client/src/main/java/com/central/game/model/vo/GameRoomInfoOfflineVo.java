package com.central.game.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 现场桌台信息详情
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("现场桌台信息")
public class GameRoomInfoOfflineVo {

    @ApiModelProperty(value = "游戏ID")
    private Long gameId;

    @ApiModelProperty(value = "游戏名称")
    private String gameName;

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
