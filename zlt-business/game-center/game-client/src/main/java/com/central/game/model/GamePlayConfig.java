package com.central.game.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@TableName("game_play_config")
@ApiModel("游戏玩法配置")
public class GamePlayConfig extends SuperEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "游戏ID")
    private Long gameId;

    @ApiModelProperty(value = "玩法名称")
    private String playName;

    @ApiModelProperty(value = "赔率:格式1:1")
    private String odds;

    @ApiModelProperty(value = "牌靴局数限制")
    private Integer bureauLimit;

    @ApiModelProperty(value = "游戏状态 0:禁用，1:启用")
    private Integer status;
}
