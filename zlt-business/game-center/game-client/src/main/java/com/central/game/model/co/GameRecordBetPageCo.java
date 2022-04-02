package com.central.game.model.co;

import com.central.common.model.co.PageCo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("投注记录入参")
public class GameRecordBetPageCo extends PageCo {

    @ApiModelProperty(value = "日期类型：0.今天，1.本周，2.上周", required = true)
    @NotNull(message = "日期类型不能为空")
    private String type;

    @ApiModelProperty(hidden = true)
    private String startTime;

    @ApiModelProperty(hidden = true)
    private String endTime;

    @ApiModelProperty(hidden = true)
    private Long userId;

}
