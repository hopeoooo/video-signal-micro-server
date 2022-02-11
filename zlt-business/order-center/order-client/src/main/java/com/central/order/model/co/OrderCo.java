package com.central.order.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class OrderCo {

    private Long id;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "订单类型")
    private Integer type;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "备注")
    private String remark;

}
