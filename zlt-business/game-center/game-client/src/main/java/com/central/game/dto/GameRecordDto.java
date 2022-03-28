package com.central.game.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GameRecordDto {

	@ApiModelProperty(value = "下注金额")
	private BigDecimal betAmount;

	@ApiModelProperty(value = "有效下注")
	private BigDecimal validbet;

	@ApiModelProperty(value = "派彩金额")
	private BigDecimal winningAmount;

	@ApiModelProperty(value = "输赢金额")
	private BigDecimal winLoss;

}
