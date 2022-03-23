package com.central.game.model.co;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class GameListCo {

    private Long id;
    @ApiModelProperty(value = "游戏名称")
    private String name;
    @ApiModelProperty(value = "游戏状态 0：禁用，1：正常，2：维护")
    private Integer gameStatus;
    @ApiModelProperty(value = "返水比例(%)")
    private BigDecimal gameRate;
    @ApiModelProperty(value = "返水状态 0：禁用，1：启用")
    private Integer rateStatus;
    @ApiModelProperty(value = "在线人数")
    private Integer onlineNum = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "维护开始时间")
    private Date maintainStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "维护结束时间")
    private Date maintainEnd;
}
