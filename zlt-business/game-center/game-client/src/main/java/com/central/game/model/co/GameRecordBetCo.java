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

    @ApiModelProperty(value = "用户名", required = false)
    private String userName;

    @ApiModelProperty(value = "局号")
    private String bureauNum;

    @ApiModelProperty(value = "桌台编号")
    private String tableNum;

    @ApiModelProperty(value = "父级", required = false)
    private String  parent;

    @ApiModelProperty(value = "开始时间", required = false)
    private String  startTime;

    @ApiModelProperty(value = "结束时间", required = false)
    private String endTime;

}
