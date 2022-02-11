package com.central.config.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class BannerUpdateStateCo {

    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id not null")
    private Long id;

    @ApiModelProperty(value = "状态", required = true)
    @NotNull(message = "state not null")
    private Boolean state;

}
