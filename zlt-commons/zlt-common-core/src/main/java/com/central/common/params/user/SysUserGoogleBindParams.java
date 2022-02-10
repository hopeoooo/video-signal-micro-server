package com.central.common.params.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SysUserGoogleBindParams extends SysUserParams{

    @ApiModelProperty(value = "验证码" , required = true)
    private String googleCode;

}
