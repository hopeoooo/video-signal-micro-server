package com.central.chat.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("聊天信息")
public class MessageVo {

    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "消息内容")
    private String message;
    @ApiModelProperty(value = "时间")
    private String dateTime;
}
