package com.central.common.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("会员账变记录")
public class SysTansterMoneyLogVo implements Serializable {

    @ApiModelProperty(value = "表id")
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "第三方交易编号")
    private String traceId;

    @ApiModelProperty(value = "账变金额")
    private BigDecimal money;

    @ApiModelProperty(value = "账变前金额")
    private BigDecimal beforeMoney;

    @ApiModelProperty(value = "账变后金额")
    private BigDecimal afterMoney;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "订单类型，3.派彩，4.下注，5.手动入款，6.手动出款，7.领取洗码，8.商户API加点,9.商户API扣点")
    private Integer orderType;

    @ApiModelProperty(value = "订单类型名称")
    private String orderTypeName;
}
