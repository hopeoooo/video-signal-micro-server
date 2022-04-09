package com.central.game.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("即时彩池入参")
public class GameRecordLivePotCo {

    @ApiModelProperty(value = "游戏ID", required = true)
    @NotNull(message = "游戏ID不能为空")
    private Long gameId;

    @ApiModelProperty(value = "桌台编号", required = true)
    @NotBlank(message = "桌台编号不能为空")
    private String tableNum;

   /* @ApiModelProperty(value = "靴号", required = true)
    @NotBlank(message = "靴号不能为空")
    private String bootNum;

    @ApiModelProperty(value = "局号", required = true)
    @NotBlank(message = "局号不能为空")
    private String bureauNum;*/
}
