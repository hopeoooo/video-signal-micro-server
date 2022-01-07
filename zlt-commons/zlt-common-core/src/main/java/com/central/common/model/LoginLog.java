package com.central.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @author zlt
 * 用户日志实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("login_log")
@ApiModel("用户日志实体")
public class LoginLog extends SuperEntity {
	private static final long serialVersionUID = -5886012896705137070L;

	@ApiModelProperty(value = "用户名")
	private String platName;
	@ApiModelProperty(value = "用户id")
	private Long userId;
	@ApiModelProperty(value = "登录时间")
	private Date loginTime;
	@ApiModelProperty(value = "登录ip")
	private String loginIp;

}
