package com.central.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户日志查询")
public class LoginLogPageDto{
    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "状态：0.禁用，1.启用")
    private Integer enabled;
    @ApiModelProperty(value = "登录时间")
    private String loginTime;
    @ApiModelProperty(value = "登录ip")
    private String loginIp;
}
