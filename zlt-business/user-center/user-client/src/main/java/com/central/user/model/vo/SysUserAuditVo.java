package com.central.user.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("稽核记录")
public class SysUserAuditVo {

    private Long id;

    @ApiModelProperty(value = "会员账号")
    private String userName;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "稽核类型，5：手动入款")
    private Integer auditType;

    @ApiModelProperty(value = "稽核名称")
    private String orderTypeName;

    @ApiModelProperty(value = "订单状态，1：未完成，2：已完成")
    private Integer orderType;

    @ApiModelProperty(value = "稽核金额")
    private BigDecimal auditAmount;

    @ApiModelProperty(value = "未完成有效打码")
    private BigDecimal undoneValidBet;

    @ApiModelProperty(value = "已完成打码")
    private BigDecimal doneValidBet;

    @ApiModelProperty(value = "剩余有效打码")
    private BigDecimal residueValidBet;

    @ApiModelProperty(value = "账户余额")
    private BigDecimal userAmount;

    @ApiModelProperty(value = "可提现金额")
    private BigDecimal withdrawAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
