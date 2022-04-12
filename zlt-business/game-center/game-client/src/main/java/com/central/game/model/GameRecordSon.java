package com.central.game.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("game_record_son")
@ApiModel("游戏下注记录子表")
public class GameRecordSon extends SuperEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "游戏记录表ID")
    private Long gameRecordId;

    @ApiModelProperty(value = "盈利后是否加回本地钱包，1.是")
    private Integer addMoneyStatus;


}
