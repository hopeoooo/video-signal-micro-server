package com.central.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("关注列表")
@Data
public class RoomFollowVo {

    @ApiModelProperty(value = "房间ID")
    private Long roomId;

    @ApiModelProperty(value = "房间名称")
    private String gameRoomName;
}
