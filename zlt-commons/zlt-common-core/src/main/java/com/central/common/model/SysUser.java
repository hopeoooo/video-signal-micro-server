package com.central.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zlt
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
@ApiModel("用户实体")
public class SysUser extends SuperEntity {
	private static final long serialVersionUID = -5886012896705137070L;

	@ApiModelProperty(value = "用户名")
	private String username;
	@ApiModelProperty(value = "密码")
	private String password;
	@ApiModelProperty(value = "昵称")
	private String nickname;
	@ApiModelProperty(value = "头像地址")
	private String headImgUrl;
	@ApiModelProperty(value = "手机号")
	private String mobile;
	@ApiModelProperty(value = "性别")
	private Integer sex;
	@ApiModelProperty(value = "状态：0.禁用，1.启用")
	private Boolean enabled;
	@ApiModelProperty(value = "账号类型：APP：前端app用户，APP_GUEST：前端app游客用户，BACKEND：后端管理用户")
	private String type;
	private String openId;
	
	@ApiModelProperty(value = "逻辑删除 0：未删除，1：已删除")
	private boolean isDel;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "逻辑删除 0：未删除，1：已删除")
	private boolean isLogin;

	@ApiModelProperty(value = "逻辑删除 0：未删除，1：已删除")
	private boolean isBet;

	@ApiModelProperty(value = "逻辑删除 0：未删除，1：已删除")
	private String loginIp;

	@ApiModelProperty(value = "投注自动提交 开启：true,关闭：false")
	private Boolean isAutoBet;

	@ApiModelProperty(value = "谷歌验证码KEY")
	private String gaKey;

	@ApiModelProperty(value = "谷歌验证码是否绑定1 1：已绑定，其他：未绑定")
	private Integer gaBind;

	@ApiModelProperty(value = "是否验证 1：是，其他：否")
	private Integer verify;

	@ApiModelProperty(value = "父级")
	private String parent;

	@TableField(exist = false)
	private List<SysRole> roles;
	@TableField(exist = false)
	private String roleId;
	@TableField(exist = false)
	private String oldPassword;
	@TableField(exist = false)
	private String newPassword;



	@ApiModelProperty(value = "当前余额")
	@TableField(exist = false)
	private BigDecimal currentBalance;


	@JsonFormat(shape = JsonFormat.Shape.STRING)
	public BigDecimal getCurrentBalance(){
		return currentBalance == null ? BigDecimal.ZERO.setScale(2) : currentBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

}
