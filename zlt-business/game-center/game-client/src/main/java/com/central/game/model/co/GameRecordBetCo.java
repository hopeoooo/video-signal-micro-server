package com.central.game.model.co;

import com.central.common.model.co.PageCo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("下注结果入参")
public class GameRecordBetCo extends PageCo {

    @ApiModelProperty(value = "游戏Id", required = false)
    private Long gameId;

    @ApiModelProperty(value = "注单号", required = false)
    private String betId;

    @ApiModelProperty(value = "用户名", required = false)
    private String userName;

    @ApiModelProperty(value = "局号")
    private String bureauNum;

    @ApiModelProperty(value = "桌台编号")
    private String tableNum;

    @ApiModelProperty(value = "父级", required = false)
    private String  parent;

    @ApiModelProperty(value = "投注开始时间", required = false)
    private String  startTime;

    @ApiModelProperty(value = "投注结束时间", required = false)
    private String endTime;

    @ApiModelProperty(value = "开奖开始时间", required = false)
    private String  setStartTime;

    @ApiModelProperty(value = "开奖结束时间", required = false)
    private String setEndTime;

}
