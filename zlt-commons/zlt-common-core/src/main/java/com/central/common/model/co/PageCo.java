package com.central.common.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PageCo {

    @ApiModelProperty(value = "分页起始位置", required = true)
    private Integer page;

    @ApiModelProperty(value = "分页结束位置", required = true)
    private Integer limit;

}
