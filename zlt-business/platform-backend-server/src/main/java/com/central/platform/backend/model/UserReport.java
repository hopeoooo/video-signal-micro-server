package com.central.platform.backend.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_report")
@ApiModel("会员报表")
public class UserReport implements Serializable{

    private static final long serialVersionUID = -5458727523705137070L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "会员id")
    private Long userId;

    @ApiModelProperty(value = "统计时间段(yyyy-MM-dd)")
    private String staticsTimes;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal chargeAmount;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal withdrawMoney;

    @ApiModelProperty(value = "有效投注")
    private BigDecimal validAmount;

    @ApiModelProperty(value = "输赢")
    private BigDecimal winLoss;

}
