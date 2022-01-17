package com.central.config.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 轮播图实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_banner")
@ApiModel("轮播图实体")
public class SysBanner extends SuperEntity {
    private static final long serialVersionUID = -5886012896705137070L;

	@ApiModelProperty(value = "h5图片url")
	private String h5Url;
	@ApiModelProperty(value = "h5图片url(横屏)")
	private String h5HorizontalUrl;
	@ApiModelProperty(value = "web图片url")
	private String webUrl;

	@ApiModelProperty(value = "图片关联id")
	private String h5FileId;
	@ApiModelProperty(value = "图片关联id(横屏)")
	private String h5HorizontalFileId;
	@ApiModelProperty(value = "图片关联id")
	private String webFileId;

	@ApiModelProperty(value = "状态(0:停用,1:启用)")
	private Boolean state;
	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "链接url")
	private String linkUrl;

}
