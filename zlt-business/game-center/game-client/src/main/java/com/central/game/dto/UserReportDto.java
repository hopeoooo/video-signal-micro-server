package com.central.game.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserReportDto {

    @ApiModelProperty(value = "会员id")
    private Long userId;

    @ApiModelProperty(value = "有效投注")
    private BigDecimal validAmount;

    @ApiModelProperty(value = "输赢")
    private BigDecimal winLoss;
}
