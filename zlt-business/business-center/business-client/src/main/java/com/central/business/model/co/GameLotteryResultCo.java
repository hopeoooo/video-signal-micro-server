package com.central.business.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("开奖结果入参")
public class GameLotteryResultCo {

    @ApiModelProperty(value = "开始时间,格式yyyy-MM-dd HH:mm:ss,GMT+8,开始时间不能超过当前时间60天")
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "结束时间,格式yyyy-MM-dd HH:mm:ss,GMT+8")
    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    @ApiModelProperty(value = "签名", required = true)
    @NotBlank(message = "签名不能为空")
    private String signature;

}
