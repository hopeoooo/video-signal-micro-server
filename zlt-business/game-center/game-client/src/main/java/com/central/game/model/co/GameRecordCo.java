package com.central.game.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("下注结果入参")
public class GameRecordCo {

    @ApiModelProperty(value = "桌台编号", required = true)
    @NotBlank(message = "桌台编号不能为空")
    private String tableNum;

    @ApiModelProperty(value = "靴号", required = true)
    @NotBlank(message = "靴号不能为空")
    private String bootNum;

    @ApiModelProperty(value = "局号", required = true)
    @NotBlank(message = "局号不能为空")
    private String bureauNum;

    @ApiModelProperty(value = "下注结果集", required = true)
    @NotEmpty(message = "下注结果集不能为空")
    @Valid
    private List<GameRecordBetDataCo> betResult;
}
