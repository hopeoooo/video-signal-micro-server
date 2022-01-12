package com.central.config.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zlt
 * 头像实体
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@TableName("sys_avatar_picture")
@ApiModel("头像实体")
public class SysAvatarPicture extends SuperEntity {
	private static final long serialVersionUID = -5886012896705137070L;

	@ApiModelProperty(value = "图片地址")
	private String url;


	@ApiModelProperty(value = "图片关联id")
	private String fileId;


}
