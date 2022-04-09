package com.central.game.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@ApiModel("新增下注数据")
@Data
public class NewAddLivePotVo {

    @ApiModelProperty(value = "游戏ID")
    private Long gameId;

    @ApiModelProperty(value = "桌台编号")
    private String tableNum;

    @ApiModelProperty(value = "靴号")
    private String bootNum;

    @ApiModelProperty(value = "局号")
    private String bureauNum;

    @ApiModelProperty(value = "新增下注数据")
    private List<LivePotVo> betResult;

    @ApiModelProperty(value = "新增(累计)下注人数(去重)")
    private Integer betNum = 0;

    @ApiModelProperty(value = "新增(累计)下注金额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal betAmount = BigDecimal.ZERO;
}
