package com.central.game.model.vo;

import com.central.common.utils.Decimal2Serializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OperatingOverviewVo implements Serializable {


    @ApiModelProperty(value = "注册")
    private Long registerNum;

    @ApiModelProperty(value = "充值")
    private Long rechargeNum;

    @ApiModelProperty(value = "首冲")
    private Long firstRechargeNum;

    @ApiModelProperty(value = "充值总额")
    @JsonSerialize(using = Decimal2Serializer.class, nullsUsing = Decimal2Serializer.class)
    private BigDecimal recharge=BigDecimal.ZERO;

    @ApiModelProperty(value = "提现总额")
    @JsonSerialize(using = Decimal2Serializer.class, nullsUsing = Decimal2Serializer.class)
    private BigDecimal withdrawal=BigDecimal.ZERO;

    @ApiModelProperty(value = "充提差额")
    @JsonSerialize(using = Decimal2Serializer.class, nullsUsing = Decimal2Serializer.class)
    private BigDecimal difference=BigDecimal.ZERO;

    @ApiModelProperty(value = "有效投注金额")
    @JsonSerialize(using = Decimal2Serializer.class, nullsUsing = Decimal2Serializer.class)
    private BigDecimal validbet;

    @ApiModelProperty(value = "有效投注人数")
    private Integer validbetNum;

    @ApiModelProperty(value = "盈利")
    @JsonSerialize(using = Decimal2Serializer.class, nullsUsing = Decimal2Serializer.class)
    private BigDecimal winLoss=BigDecimal.ZERO;

}
