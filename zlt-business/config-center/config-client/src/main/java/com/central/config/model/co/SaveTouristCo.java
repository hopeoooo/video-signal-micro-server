package com.central.config.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class SaveTouristCo {

    @ApiModelProperty(value = "游客携带金额", required = true)
    private BigDecimal touristAmount;

    @ApiModelProperty(value = "游客单笔最大投注", required = true)
    private BigDecimal touristSingleMaxBet;

}
