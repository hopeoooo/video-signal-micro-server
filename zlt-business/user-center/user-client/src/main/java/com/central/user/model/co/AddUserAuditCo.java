package com.central.user.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel
public class AddUserAuditCo {

    @NotNull(message = "会员账号不能为空")
    @ApiModelProperty(value = "会员账号")
    private String userName;

    @NotNull(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "增加打码量打码")
    private BigDecimal betAmount;
}
