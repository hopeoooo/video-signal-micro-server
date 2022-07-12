package com.central.game.model.co;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class GameRoomListCo {

    private Long id;
    @ApiModelProperty(value = "游戏表id")
    private Long gameId;

    @ApiModelProperty(value = "游戏桌台名称")
    private String gameRoomName;

    @ApiModelProperty(value = "桌台英文名称")
    private String enName;
    @ApiModelProperty(value = "桌台高棉语名称")
    private String khmName;
    @ApiModelProperty(value = "桌台泰语名称")
    private String thName;

    @ApiModelProperty(value = "游戏房间状态 0禁用，1：正常，2：维护")
    private Integer roomStatus;

    @ApiModelProperty(value = "返佣类型1：免佣，2：抽佣")
    private Integer commissionType;

    @ApiModelProperty(value = "庄闲最小限红")
    private BigDecimal minBankerPlayer;

    @ApiModelProperty(value = "庄闲最大限红")
    private BigDecimal maxBankerPlayer;

    @ApiModelProperty(value = "和最小限红")
    private BigDecimal minSum;

    @ApiModelProperty(value = "和最大限红")
    private BigDecimal maxSum;

    @ApiModelProperty(value = "对子最小限红")
    private BigDecimal minTwain;

    @ApiModelProperty(value = "对子最大限红")
    private BigDecimal maxTwain;


    @ApiModelProperty(value = "近景视频url地址")
    private String nearVideoUrl;

    @ApiModelProperty(value = "远景视频url地址")
    private String farVideoUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "维护开始时间")
    private Date maintainStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "维护结束时间")
    private Date maintainEnd;
}
