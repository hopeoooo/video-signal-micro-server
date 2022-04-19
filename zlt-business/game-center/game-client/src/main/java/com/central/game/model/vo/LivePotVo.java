package com.central.game.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel("即时彩池")
@Data
public class LivePotVo {

    @ApiModelProperty(value = "用户ID", hidden = true)
    @JsonIgnore
    private Long userId;

    @ApiModelProperty(value = "用户名", hidden = true)
    @JsonIgnore
    private String userName;

    @ApiModelProperty(value = "玩法代码")
    private String betCode;

    @ApiModelProperty(value = "玩法名称")
    private String betName;

    @ApiModelProperty(value = "本局累计下注额")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal betAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "下注人数")
    private Integer betNum = 0;
}
