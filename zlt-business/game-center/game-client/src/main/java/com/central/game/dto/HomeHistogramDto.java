package com.central.game.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HomeHistogramDto implements Serializable {

    private static final long serialVersionUID = -6877182699817195379L;

    @ApiModelProperty(value = "有效投注")
    private BigDecimal validbet;

    @ApiModelProperty(value = "公司总盈亏")
    private BigDecimal profitAndLoss;

}
