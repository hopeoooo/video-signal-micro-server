package com.central.game.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UserGameReportDto implements Serializable {

    private static final long serialVersionUID = -6845823669981715379L;

    @ApiModelProperty(value = "游戏名称")
    private String gameName;

    @ApiModelProperty(value = "总注单量")
    private Integer betNum;

    @ApiModelProperty(value = "投注金额")
    private BigDecimal betAmount;

    @ApiModelProperty(value = "有效投注")
    private BigDecimal validAmount;

    @ApiModelProperty(value = "输赢")
    private BigDecimal winLoss;

    @ApiModelProperty(value = "占单量")
    private BigDecimal betNumRatio;

    @ApiModelProperty(value = "获利比")
    private BigDecimal profitRatio;

}
