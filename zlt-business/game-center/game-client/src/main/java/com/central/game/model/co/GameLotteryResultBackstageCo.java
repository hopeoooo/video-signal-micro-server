package com.central.game.model.co;

import com.central.common.model.co.PageCo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("开奖结果入参")
public class GameLotteryResultBackstageCo extends PageCo {


    @ApiModelProperty(value = "局号")
    private String bureauNum;

    @ApiModelProperty(value = "桌台编号")
    private String tableNum;

    @ApiModelProperty(value = "游戏ID")
    private String gameId;

    @ApiModelProperty(value = "开始时间", required = false)
    private String startTime;

    @ApiModelProperty(value = "结束时间", required = false)
    private String endTime;

}
