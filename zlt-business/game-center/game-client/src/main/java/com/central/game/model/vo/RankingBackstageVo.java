package com.central.game.model.vo;

import com.central.common.utils.Decimal2Serializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RankingBackstageVo {

	@ApiModelProperty(value = "用户id")
	private Long userId;
	@ApiModelProperty(value = "用户名")
	private String userName;

	@ApiModelProperty(value = "有效下注")
	@JsonSerialize(using = Decimal2Serializer.class, nullsUsing = Decimal2Serializer.class)
	private BigDecimal validbet=BigDecimal.ZERO;

	@ApiModelProperty(value = "输赢金额")
	@JsonSerialize(using = Decimal2Serializer.class, nullsUsing = Decimal2Serializer.class)
	private BigDecimal winLoss=BigDecimal.ZERO;

	@ApiModelProperty(value = "充值金额")
	@JsonSerialize(using = Decimal2Serializer.class, nullsUsing = Decimal2Serializer.class)
	private BigDecimal recharge=BigDecimal.ZERO;

	@ApiModelProperty(value = "提现金额")
	@JsonSerialize(using = Decimal2Serializer.class, nullsUsing = Decimal2Serializer.class)
	private BigDecimal withdrawal=BigDecimal.ZERO;

	@ApiModelProperty(value = "充提差额")
	@JsonSerialize(using = Decimal2Serializer.class, nullsUsing = Decimal2Serializer.class)
	private BigDecimal difference=BigDecimal.ZERO;


}
