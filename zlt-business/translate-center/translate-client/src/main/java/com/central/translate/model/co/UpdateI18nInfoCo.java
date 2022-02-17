package com.central.translate.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class UpdateI18nInfoCo extends SaveI18nInfoCo{

    @ApiModelProperty(value = "主键", required = true)
    private Long id;

}
