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

    @ApiModelProperty(value = "开始时间", required = false)
    private String  startTime;

    @ApiModelProperty(value = "结束时间", required = false)
    private String endTime;

}
