package com.central.config.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wash_code_config")
@ApiModel("洗码配置表")
public class WashCodeConfig extends SuperEntity {


    @ApiModelProperty(value = "游戏ID")
    private String gameId;

    @ApiModelProperty(value = "游戏名称")
    private String gameName;

    @ApiModelProperty(value = "游戏类型")
    private String gameType;

    @ApiModelProperty(value = "返水比例")
    private BigDecimal rate = BigDecimal.ZERO;

    @ApiModelProperty(value = "状态：0:禁用，1:启用")
    private Integer state;

}
