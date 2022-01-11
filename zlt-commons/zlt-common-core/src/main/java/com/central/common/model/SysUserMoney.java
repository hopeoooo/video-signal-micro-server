package com.central.common.model;

import com.central.common.model.SuperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.utils.Decimal2Serializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户钱包表
 *
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_money")
@ApiModel("用户钱包")
public class SysUserMoney extends SuperEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "余额")
    private BigDecimal money;

    @ApiModelProperty(value = "未完成打码量")
    private BigDecimal unfinishedCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public BigDecimal getMoney() {
        return keepDecimal(money);
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public BigDecimal getUnfinishedCode() {
        return keepDecimal(unfinishedCode);
    }

    private BigDecimal keepDecimal(BigDecimal val) {
        return val == null ? BigDecimal.ZERO.setScale(2) : val.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
