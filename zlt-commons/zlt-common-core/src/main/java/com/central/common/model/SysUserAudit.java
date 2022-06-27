package com.central.common.model;


import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 稽核记录表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_audit")
@ApiModel("稽核记录表")
public class SysUserAudit extends SuperEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "会员账号")
    private String userName;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "稽核类型，5：手动入款")
    private Integer auditType;

    @ApiModelProperty(value = "订单状态，1：未完成，2：已完成")
    private Integer orderStatus;

    @ApiModelProperty(value = "稽核金额")
    private BigDecimal auditAmount;

    @ApiModelProperty(value = "所需有效打码")
    private BigDecimal undoneValidBet;

    @ApiModelProperty(value = "已完成打码")
    private BigDecimal doneValidBet;

    @ApiModelProperty(value = "剩余有效打码")
    private BigDecimal residueValidBet;

    @ApiModelProperty(value = "账户余额")
    private BigDecimal userAmount;

    @ApiModelProperty(value = "可提现金额")
    private BigDecimal withdrawAmount;

}
