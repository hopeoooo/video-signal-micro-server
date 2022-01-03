package com.central.order.model;

import com.central.common.model.SuperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 订单表
 *
 * @author zlt
 * @date 2022-01-03 18:27:12
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@TableName("orders")
@ApiModel("订单表")
public class Orders extends SuperEntity {
    private static final long serialVersionUID = 1L;

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
