package com.central.common.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;


// TODO 这些moulde移到 user-client里面

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_wash_code_config")
@ApiModel("个人洗码配置表")
public class UserWashCodeConfig extends SuperEntity {

    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @ApiModelProperty(value = "游戏ID")
    private Long gameId;
    @ApiModelProperty(value = "游戏名称")
    private String gameName;
    @ApiModelProperty(value = "游戏英文名称")
    private String gameEnName;
    @ApiModelProperty(value = "返水比例")
    private BigDecimal gameRate = BigDecimal.ZERO;
}
