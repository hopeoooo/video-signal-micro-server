package com.central.user.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SysUserParamsCo {

    @ApiModelProperty(value = "管理员账号" , required = true)
    private String username;

    @ApiModelProperty(value = "管理员密码" , required = true)
    private String password;
}
