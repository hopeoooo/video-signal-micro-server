package com.central.game.model;

import com.central.common.model.SuperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@TableName("game_list")
@ApiModel("游戏列表")
public class GameList extends SuperEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "游戏名称")
    private String name;
}
