package com.central.common.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_tanster_money_log")
@ApiModel("账变记录")
public class SysTansterMoneyLog extends SuperEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "第三方交易编号")
    private String traceId;

    @ApiModelProperty(value = "注单编号")
    private String betId;

    @ApiModelProperty(value = "账变金额")
    private BigDecimal money;

    @ApiModelProperty(value = "账变前金额")
    private BigDecimal beforeMoney;

    @ApiModelProperty(value = "账变后金额")
    private BigDecimal afterMoney;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "订单类型")
    private Integer orderType;

    @ApiModelProperty(value = "父级")
    private String parent;
}
