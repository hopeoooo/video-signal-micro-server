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
	@ApiModelProperty(value = "web图片url")
	private String webUrl;

	@ApiModelProperty(value = "图片关联id")
	private String h5FileId;
	@ApiModelProperty(value = "图片关联id")
	private String webFileId;

	@ApiModelProperty(value = "状态(0:停用,1:启用)")
	private Boolean state;
	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "链接url")
	private String linkUrl;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "开始时间")
	private Date startTime;

	@ApiModelProperty(value = "开始方式(0:即时,1:定时)")
	private Integer startMode;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "结束时间")
	private Date endTime;

	@ApiModelProperty(value = "结束方式(0:长期,1:到期)")
	private Integer endMode;


}
