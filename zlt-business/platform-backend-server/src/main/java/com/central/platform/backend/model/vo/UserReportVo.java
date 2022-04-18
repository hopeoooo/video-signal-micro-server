package com.central.platform.backend.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UserReportVo implements Serializable {

    private static final long serialVersionUID = -6874829398174187589L;

    @ApiModelProperty(value = "会员id")
    private Long userId;

    @ApiModelProperty(value = "会员账号")
    private String username;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal chargeAmount;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal withdrawMoney;

    @ApiModelProperty(value = "有效投注")
    private BigDecimal validAmount;

    @ApiModelProperty(value = "输赢")
    private BigDecimal winLoss;
}
