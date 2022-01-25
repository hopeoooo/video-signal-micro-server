package com.central.game.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@TableName("game_room_list")
@ApiModel("房间列表")
public class GameRoomList extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "游戏表id")
    private Long gameId;

    @ApiModelProperty(value = "游戏房间名称")
    private String gameRoomName;

    @ApiModelProperty(value = "游戏房间状态 0禁用，1：正常，2：维护")
    private Integer roomStatus;

    @ApiModelProperty(value = "返佣类型1：免佣，2：抽佣")
    private Integer commissionType;

    @ApiModelProperty(value = "限红单局下注最小金额")
    private BigDecimal betMin;

    @ApiModelProperty(value = "限红单局总下注最大金额")
    private BigDecimal betMax;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "维护开始时间")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date maintainStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "维护结束时间")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date maintainEnd;
}
