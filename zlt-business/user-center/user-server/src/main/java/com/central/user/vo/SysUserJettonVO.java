package com.central.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("筹码配置")
@Data
public class SysUserJettonVO {

    @ApiModelProperty(value = "筹码配置内容")
    private String jetton_config;
}
