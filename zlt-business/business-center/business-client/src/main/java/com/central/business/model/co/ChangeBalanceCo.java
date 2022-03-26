package com.central.business.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("加扣点")
public class ChangeBalanceCo {

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String userName;

    @ApiModelProperty(value = "金额（大于0）", required = true)
    @NotNull(message = "金额不能为空")
    private BigDecimal money;

    @ApiModelProperty(value = "加扣点类型：8.加点，9.扣点", required = true)
    @NotNull(message = "加扣点类型不能为空")
    private Integer type;

    @ApiModelProperty(value = "交易编号", required = false)
    private String traceId;

    @ApiModelProperty(value = "签名", required = true)
    @NotBlank(message = "签名不能为空")
    private String signature;

}
