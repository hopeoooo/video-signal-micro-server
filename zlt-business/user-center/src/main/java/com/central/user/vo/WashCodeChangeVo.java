package com.central.user.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 用户洗码明细
 *
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
@Data
@ApiModel("用户洗码明细")
public class WashCodeChangeVo{

    @ApiModelProperty(value = "游戏ID")
    private String gameId;
    @ApiModelProperty(value = "游戏名称")
    private String gameName;
    @ApiModelProperty(value = "返水比例（%）")
    private BigDecimal rate;
    @ApiModelProperty(value = "有效投注额")
    private BigDecimal validbet;
    @ApiModelProperty(value = "返水金额")
    private BigDecimal amount;
}
