package com.central.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("用户金额")
public class SysMoneyVO {

    @ApiModelProperty(value = "用户id")
    private Long uid;

    @ApiModelProperty(value = "用户金额")
    private BigDecimal userMoney;
}
