package com.central.game.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("桌台用户分组")
public class GameRoomGroupCo {

    @ApiModelProperty(value = "游戏ID", required = true)
    @NotNull(message = "游戏ID不能为空")
    private Long gameId;

    @ApiModelProperty(value = "桌台编号", required = true)
    @NotBlank(message = "桌台编号不能为空")
    private String tableNum;
}
