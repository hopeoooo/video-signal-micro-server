package com.central.translate.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel
@Data
public class I18nPositionCo {

    @ApiModelProperty(required = false, value = "主键，不传为新增")
    private Long id;

    @ApiModelProperty(required = true, value = "位置名称")
    @NotBlank(message = "位置名称不能为空")
    private String name;

    @ApiModelProperty(required = false, value = "是否是页面 默认 false")
    private Boolean isPage = false;

    @ApiModelProperty(required = false, value = "isPage = true时需要指定pid")
    private Long pid;
}
