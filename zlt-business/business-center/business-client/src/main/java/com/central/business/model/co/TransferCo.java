package com.central.business.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("加扣点")
public class TransferCo {

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "交易编号")
    private String traceId;

    @ApiModelProperty(value = "开始时间,格式yyyy-MM-dd HH:mm:ss,GMT+8")
    private String startTime;

    @ApiModelProperty(value = "结束时间,格式yyyy-MM-dd HH:mm:ss,GMT+8")
    private String endTime;

    @ApiModelProperty(value = "签名", required = true)
    @NotBlank(message = "签名不能为空")
    private String signature;

}
