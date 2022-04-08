package com.central.game.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("开奖结果")
@Data
public class LotteryResultVo {

    @ApiModelProperty(value = "玩法代码")
    private String betCodes;

    @ApiModelProperty(value = "玩法名称")
    private String betNames;
}
