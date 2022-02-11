package com.central.user.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SysUserGoogleBindCoCo extends SysUserParamsCo {

    @ApiModelProperty(value = "验证码" , required = true)
    private String googleCode;

}
