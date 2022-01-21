package com.central.common.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_wash_code_config")
@ApiModel("个人洗码配置表")
public class UserWashCodeConfig extends SuperEntity {

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "百家乐返水比例")
    private BigDecimal baccarat = BigDecimal.ZERO;

    @ApiModelProperty(value = "龙虎返水比例")
    private BigDecimal dragonTiger = BigDecimal.ZERO;

    @ApiModelProperty(value = "轮盘返水比例")
    private BigDecimal roulette = BigDecimal.ZERO;

    @ApiModelProperty(value = "骰宝返水比例")
    private BigDecimal sicBo = BigDecimal.ZERO;

    @ApiModelProperty(value = "色碟返水比例")
    private BigDecimal seDie = BigDecimal.ZERO;
}
