package com.central.config.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UpdateNoticeCo {

    @ApiModelProperty(value = "公告id", required = true)
    private Long id;

    @ApiModelProperty(value = "状态", required = true)
    private Boolean state;

}
