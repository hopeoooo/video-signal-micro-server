package com.central.game.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("下注结果入参")
public class GameLotteryResultCo {

    @ApiModelProperty(value = "开始时间", required = false)
    private String startTime;

    @ApiModelProperty(value = "结束时间", required = false)
    private String endTime;
}
