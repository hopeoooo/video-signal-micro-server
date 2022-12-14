package com.central.game.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 开奖结果
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("game_lottery_result")
@ApiModel("游戏开奖结果")
public class GameLotteryResult extends SuperEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "游戏ID")
    private Long gameId;

    @ApiModelProperty(value = "游戏名称")
    private String gameName;

    @ApiModelProperty(value = "机器编号")
    private String machineCode;

    @ApiModelProperty(value = "桌台编号")
    private String tableNum;

    @ApiModelProperty(value = "靴号")
    private String bootNum;

    @ApiModelProperty(value = "局号")
    private String bureauNum;

    @ApiModelProperty(value = "开奖结果ID")
    private Long lotteryId;

    @ApiModelProperty(value = "开奖结果编号,闲:1,庄:4,和:7,庄闲对:2,闲对:5,庄对:8,大:9,小:3")
    private String result;

    @ApiModelProperty(value = "开奖结果名称")
    private String resultName;

    @ApiModelProperty(value = "开奖时间")
    private String lotteryTime;
}
