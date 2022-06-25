package com.central.config.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BetMultipleDto {

    @ApiModelProperty(value = "充值打码倍数")
    private BigDecimal betMultiple;


    @ApiModelProperty(value = "打码清零点")
    private BigDecimal betZrrorPint;

}
