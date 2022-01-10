package com.central.config.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("logo地址")
public class logoUrlDto {


	@ApiModelProperty("网站icon")
	private String websiteIcon;

	@ApiModelProperty("(PC)log图片地址")
	private String logImageUrlPc;

	@ApiModelProperty("(APP)log图片地址")
	private String logImageUrlApp;

	@ApiModelProperty("(APP)登录注册页log图片地址")
	private String loginRegisterLogImageUrlApp;



}
