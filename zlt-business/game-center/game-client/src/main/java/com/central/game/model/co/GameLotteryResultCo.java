package com.central.game.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("开奖结果入参")
public class GameLotteryResultCo {

    @ApiModelProperty(value = "开奖数据ID", required = false)
    private Long id;

    @ApiModelProperty(value = "机器编号")
    private String machineCode;

    @ApiModelProperty(value = "桌台编号")
    private String tableNum;

    @ApiModelProperty(value = "靴号")
    private String bootNum;

    @ApiModelProperty(value = "局号")
    private String bureauNum;

    @ApiModelProperty(value = "开奖结果ID")
    private Long lotteryId;

    @ApiModelProperty(value = "开奖结果编号")
    private String result;

    @ApiModelProperty(value = "开奖结果名称")
    private String resultName;

    @ApiModelProperty(value = "开奖时间")
    private String createTime;

    @ApiModelProperty(value = "开始时间", required = false)
    private String startTime;

    @ApiModelProperty(value = "结束时间", required = false)
    private String endTime;
}
