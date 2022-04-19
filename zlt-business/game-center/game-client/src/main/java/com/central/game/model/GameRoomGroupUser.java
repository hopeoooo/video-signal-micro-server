package com.central.game.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@TableName("game_room_group_user")
@ApiModel("桌台分组用户")
public class GameRoomGroupUser extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分组id")
    private Long groupId;

    @ApiModelProperty(value = "用户ID")
    private Long userId;
}
