package com.central.config.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class FindNoticeCo {

    @ApiModelProperty(value = "类型(1:一般,2:维护,3:系统)")
    private Integer type;

    @ApiModelProperty(value = "状态(0:停用,1:启用)")
    private Integer state;

}
