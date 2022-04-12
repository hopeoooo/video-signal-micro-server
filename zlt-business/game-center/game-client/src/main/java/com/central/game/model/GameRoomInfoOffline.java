package com.central.game.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 现场桌台信息详情
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("game_room_info_offline")
@ApiModel("现场桌台信息详情")
public class GameRoomInfoOffline extends SuperEntity {
    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty(value = "庄闲限红小", hidden = true)
    @JsonIgnore
    private Integer bankerAndPlayerSmall;

    @ApiModelProperty(value = "庄闲限红大", hidden = true)
    @JsonIgnore
    private Integer bankerAndPlayerMax;

    @ApiModelProperty(value = "和限红小", hidden = true)
    @JsonIgnore
    private Integer drawSmall;

    @ApiModelProperty(value = "和限红大", hidden = true)
    @JsonIgnore
    private Integer drawMax;

    @ApiModelProperty(value = "对子限红小", hidden = true)
    @JsonIgnore
    private Integer doubleSmall;

    @ApiModelProperty(value = "对子限红大", hidden = true)
    @JsonIgnore
    private Integer doubleMax;

    @ApiModelProperty(value = "牌局状态 0洗牌中 1开始下注 2停止下注 4结算完成")
    private Integer status;

    @ApiModelProperty(value = "开始下注时间")
    private Date startTime;

    @ApiModelProperty(value = "开奖结束时间")
    private Date endTime;

    @ApiModelProperty(value = "数据修改时间，时间戳", hidden = true)
    private Long times;
}
