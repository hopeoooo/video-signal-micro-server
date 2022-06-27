package com.central.common.model;


import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 稽核记录明细
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_audit_detail")
@ApiModel("稽核记录明细")
public class SysUserAuditDetail extends SuperEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "会员账号")
    private String userName;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "稽核ID")
    private Long auditId;

    @ApiModelProperty(value = "游戏注单表ID")
    private Long gameRecordId;

    @ApiModelProperty(value = "注单号")
    private String betId;

    @ApiModelProperty(value = "变化打码量")
    private BigDecimal amount;

    @ApiModelProperty(value = "打码量变化前")
    private BigDecimal amountBefore;

    @ApiModelProperty(value = "打码量变化后")
    private BigDecimal amountAfter;

    @ApiModelProperty(value = "打码量清零点")
    private BigDecimal betZrrorPint;

}
