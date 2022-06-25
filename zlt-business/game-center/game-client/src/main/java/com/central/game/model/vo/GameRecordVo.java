package com.central.game.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("投注记录")
public class GameRecordVo {

    @ApiModelProperty(value = "结算时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date setTime;

    @ApiModelProperty(value = "游戏ID")
    private String gameId;

    @ApiModelProperty(value = "桌台编号")
    private String tableNum;

    @ApiModelProperty(value = "靴号")
    private String bootNum;

    @ApiModelProperty(value = "局号")
    private String bureauNum;

    @ApiModelProperty(value = "游戏名称")
    private String gameName;

    @ApiModelProperty(value = "下注金额")
    private BigDecimal betAmount;

    @ApiModelProperty(value = "有效下注")
    private BigDecimal validbet;

    @ApiModelProperty(value = "输赢金额")
    private BigDecimal winLoss;

}
