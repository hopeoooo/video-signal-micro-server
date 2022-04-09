package com.central.game.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel("派彩结果")
@Data
public class PayoutResultVo {

    @ApiModelProperty(value = "游戏ID")
    private String gameId;

    @ApiModelProperty(value = "游戏名称")
    private String gameName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "派彩金额")
    private BigDecimal winningAmount;

    @ApiModelProperty(value = "桌台编号")
    private String tableNum;

    @ApiModelProperty(value = "靴号")
    private String bootNum;

    @ApiModelProperty(value = "局号")
    private String bureauNum;
}
