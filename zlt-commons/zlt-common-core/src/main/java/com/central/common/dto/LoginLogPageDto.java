package com.central.common.dto;

import com.central.common.model.SuperPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("用户日志查询")
public class LoginLogPageDto extends SuperPage {
    @ApiModelProperty(value = "用户id")
    private String userId;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "状态：0.禁用，1.启用")
    private Integer enabled;
    @ApiModelProperty(value = "登录时间")
    private String loginTime;
    @ApiModelProperty(value = "登录ip")
    private String loginIp;
}
