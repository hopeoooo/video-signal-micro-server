package com.central.business.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Data
@ApiModel("游戏下注记录")
public class GameRecordVo {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "游戏ID")
    private String gameId;

    @ApiModelProperty(value = "游戏名称")
    private String gameName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "注单号")
    private String betId;

    @ApiModelProperty(value = "下注时间")
    private Date betTime;

    @ApiModelProperty(value = "结算时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date setTime;

    @ApiModelProperty(value = "下注金额")
    private BigDecimal betAmount;

    @ApiModelProperty(value = "有效下注")
    private BigDecimal validbet;

    @ApiModelProperty(value = "派彩金额")
    private BigDecimal winningAmount;

    @ApiModelProperty(value = "输赢金额")
    private BigDecimal winLoss;

    @ApiModelProperty(value = "下注代码")
    private String betCode;

    @ApiModelProperty(value = "下注内容")
    private String betName;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "桌台编号")
    private String tableNum;

    @ApiModelProperty(value = "靴号")
    private String bootNum;

    @ApiModelProperty(value = "局号")
    private String bureauNum;

    @ApiModelProperty(value = "牌型ex:庄:♦3♦3 闲:♥9♣10")
    private String gameResult;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
