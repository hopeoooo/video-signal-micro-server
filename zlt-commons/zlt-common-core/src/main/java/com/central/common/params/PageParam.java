package com.central.common.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("分页参数")
@Data
public class PageParam {

    @ApiModelProperty("第几页")
    private Integer page;

    @ApiModelProperty("每页条数")
    private Integer limit;

}
