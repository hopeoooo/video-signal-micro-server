package com.central.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户基本信息")
@Data
public class UserInfoVo {

    @ApiModelProperty(value = "用户名")
    private String username;
}
