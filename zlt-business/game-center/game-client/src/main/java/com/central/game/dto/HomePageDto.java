package com.central.game.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HomePageDto implements Serializable {

    private static final long serialVersionUID = 6845823678855379L;

    @ApiModelProperty(value = "有效投注")
    private BigDecimal validbet;

    @ApiModelProperty(value = "投注人数")
    private Integer betAmountNum;

    @ApiModelProperty(value = "公司总盈亏")
    private BigDecimal profitAndLoss;
}
