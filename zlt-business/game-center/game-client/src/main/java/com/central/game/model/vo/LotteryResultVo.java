package com.central.game.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("开奖结果")
@Data
public class LotteryResultVo {

    @ApiModelProperty(value = "游戏ID")
    private String gameId;

    @ApiModelProperty(value = "游戏名称")
    private String gameName;

    @ApiModelProperty(value = "机器编号")
    private String machineCode;

    @ApiModelProperty(value = "桌台编号")
    private String tableNum;

    @ApiModelProperty(value = "靴号")
    private String bootNum;

    @ApiModelProperty(value = "局号")
    private String bureauNum;

    @ApiModelProperty(value = "玩法代码")
    private String betCodes;

    @ApiModelProperty(value = "玩法名称")
    private String betNames;
}
