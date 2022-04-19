package com.central.game.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GameRecordReportDto {

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "人数")
    private Integer num;

    @ApiModelProperty(value = "投注盈亏")
    private BigDecimal profit;
}
