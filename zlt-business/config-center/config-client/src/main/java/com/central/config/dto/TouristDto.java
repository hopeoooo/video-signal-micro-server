package com.central.config.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TouristDto {

	@ApiModelProperty(value = "游客携带金额")
	private BigDecimal touristAmount;
	@ApiModelProperty(value = "游客单笔最大投注")
	private BigDecimal touristSingleMaxBet;



}
