package com.central.config.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UpdateNoticeCo {

    @ApiModelProperty(value = "公告id", required = true)
    @NotNull(message = "主键不能为空")
    private Long id;

    @ApiModelProperty(value = "状态", required = true)
    @NotNull(message = "状态不能为空")
    private Boolean state;

}
