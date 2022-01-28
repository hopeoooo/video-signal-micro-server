package com.central.common.params.translate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class I18nPositionParam {

    @ApiModelProperty(required = false, value = "主键，不传为新增")
    private Long id;

    @ApiModelProperty(required = true, value = "位置名称")
    private String name;

    @ApiModelProperty(required = false, value = "是否是页面 默认 false")
    private Boolean isPage = false;

    @ApiModelProperty(required = false, value = "isPage = true时需要指定pid")
    private Long pid;
}
