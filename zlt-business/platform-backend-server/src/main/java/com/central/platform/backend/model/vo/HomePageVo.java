package com.central.platform.backend.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HomePageVo implements Serializable {

    private static final long serialVersionUID = 1L;
    //公司总盈亏
    @ApiModelProperty(value = "公司总盈亏")
    private BigDecimal profitAndLoss = BigDecimal.ZERO;
    //存款
    @ApiModelProperty(value = "存款")
    private BigDecimal chargeAmount = BigDecimal.ZERO;
    //存款人数
    @ApiModelProperty(value = "存款人数")
    private Integer chargeAmountNum = 0;
    //提款
    @ApiModelProperty(value = "提款")
    private BigDecimal withdrawMoney = BigDecimal.ZERO;
    //提款人数
    @ApiModelProperty(value = "提款人数")
    private Integer withdrawMoneyNum = 0;
    //有效投注
    @ApiModelProperty(value = "有效投注")
    private BigDecimal validbet = BigDecimal.ZERO;
    //今日新增玩家
    @ApiModelProperty(value = "今日新增玩家")
    private Integer newUsers = 0;
    //今日在线玩家
    @ApiModelProperty(value = "今日在线玩家")
    private Integer onlineUsers = 0;
    //投注人数
    @ApiModelProperty(value = "投注人数")
    private Integer betAmountNum = 0;
}
