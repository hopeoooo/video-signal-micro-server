package com.central.platform.backend.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SiteDataReportDto implements Serializable {

    private static final long serialVersionUID = -6875784399814895179L;

    @ApiModelProperty(value = "转入金额")
    private BigDecimal shiftAmount;

    @ApiModelProperty(value = "转入人数")
    private Integer shiftNum;

    @ApiModelProperty(value = "提款金额")
    private BigDecimal withdrawMoney;

    @ApiModelProperty(value = "提款金额人数")
    private Integer withdrawMoneyNum;

    @ApiModelProperty(value = "首次存款金额")
    private BigDecimal firstChargeAmount;

    @ApiModelProperty(value = "首次存款人数")
    private Integer firstChargeAmountNum;

    @ApiModelProperty(value = "二次存款金额")
    private BigDecimal chargeAmount;

    @ApiModelProperty(value = "二次存款人数")
    private Integer chargeAmountNum;

    @ApiModelProperty(value = "下注金额")
    private BigDecimal betAmount;

    @ApiModelProperty(value = "下注人数")
    private Integer betAmountNum;

    @ApiModelProperty(value = "有效投注金额")
    private BigDecimal validbet;

    @ApiModelProperty(value = "有效投注人数")
    private Integer validbetNum;

    @ApiModelProperty(value = "派彩金额")
    private BigDecimal winningAmount;

    @ApiModelProperty(value = "派彩人数")
    private Integer winningAmountNum;

    @ApiModelProperty(value = "盈亏")
    private BigDecimal winLoss;
}
