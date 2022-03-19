package com.central.game.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("下注结果集")
public class GameRecordBetDataCo {

    @ApiModelProperty(value = "玩法代码:庄.banker、闲.player、大.big、小.small、和.tie、庄对.bpair、闲对.ppair", required = true)
    @NotBlank(message = "玩法代码不能为空")
    private String betCode;

    @ApiModelProperty(value = "玩法名称:庄、闲、大、小、和、庄对、闲对", required = true)
    @NotBlank(message = "玩法名称不能为空")
    private String betName;

    @ApiModelProperty(value = "下注金额", required = true)
    @NotBlank(message = "下注金额不能为空")
    private String betAmount;
}
