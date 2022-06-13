package com.central.game.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("游戏在线人数")
public class GameOnlineNumVo {

    @ApiModelProperty(value = "游戏ID", hidden = true)
    @JsonIgnore
    private Long gameId;

    @ApiModelProperty(value = "在线人数")
    private Integer onlineNum;
}
