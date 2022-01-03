package com.central.common.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;


/**
 * 系统配置实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_platform_config")
@ApiModel("系统配置实体")
public class SysPlatformConfig {
	//private static final long serialVersionUID = -5886012896705137070L;
	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "游客携带金额")
	private BigDecimal touristAmount;
	@ApiModelProperty(value = "游客单笔最大投注")
	private BigDecimal touristSingleMaxBet;

}
