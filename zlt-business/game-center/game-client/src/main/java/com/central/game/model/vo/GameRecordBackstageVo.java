package com.central.game.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GameRecordBackstageVo {


	@ApiModelProperty(value = "游戏ID")
	private String gameId;

	@ApiModelProperty(value = "游戏名称")
	private String gameName;

	@ApiModelProperty(value = "用户ID")
	private Long userId;

	@ApiModelProperty(value = "用户名")
	private String userName;

	@ApiModelProperty(value = "注单号")
	private String betId;

	@ApiModelProperty(value = "下注时间")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date betTime;

	@ApiModelProperty(value = "结算时间")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date setTime;

	@ApiModelProperty(value = "下注金额")
	private BigDecimal betAmount;

	@ApiModelProperty(value = "有效下注")
	private BigDecimal validbet;

	@ApiModelProperty(value = "派彩金额")
	private BigDecimal winningAmount;

	@ApiModelProperty(value = "输赢金额")
	private BigDecimal winLoss;

	@ApiModelProperty(value = "玩法代码")
	private String betCode;

	@ApiModelProperty(value = "玩法名称")
	private String betName;

	@ApiModelProperty(value = "ip")
	private String ip;

	@ApiModelProperty(value = "桌台编号")
	private String tableNum;

	@ApiModelProperty(value = "靴号")
	private String bootNum;

	@ApiModelProperty(value = "局号")
	private String bureauNum;

	@ApiModelProperty(value = "开奖结果代码")
	private String gameResult;

	@ApiModelProperty(value = "开奖结果名称")
	private String gameResultName;

	@ApiModelProperty(value = "父级")
	private String parent;

	@ApiModelProperty(value = "最小限红")
	private BigDecimal minLimitRed;

	@ApiModelProperty(value = "最大限红")
	private BigDecimal maxLimitRed;

	@ApiModelProperty(value = "盈利后是否加回本地钱包，1.是")
	private Integer addMoneyStatus;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@ApiModelProperty(value = "游戏开始投注时间")
	private Date startTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@ApiModelProperty(value = "游戏开奖结束时间")
	private Date endTime;

}
