package com.central.translate.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class UpdateI18nInfoCo extends SaveI18nInfoCo{

    @ApiModelProperty(value = "主键", required = true)
    @NotNull(message = "主键不能为空", groups = {Update.class})
    private Long id;

}
