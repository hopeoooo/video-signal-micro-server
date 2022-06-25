package com.central.config.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel
public class BetMultipleCo {

    @ApiModelProperty(value = "打码量倍数", required = true)
    @NotNull(message = "打码量倍数不为空")
    private BigDecimal betMultiple;


    @ApiModelProperty(value = "打码清零点", required = true)
    @NotNull(message = "打码清零点")
    private BigDecimal betZrrorPint;
}
