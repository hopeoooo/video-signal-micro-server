package com.central.user.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class SysUserGoogleBindCoCo extends SysUserParamsCo {

    @ApiModelProperty(value = "验证码" , required = true)
    @NotNull
    private String googleCode;

}
