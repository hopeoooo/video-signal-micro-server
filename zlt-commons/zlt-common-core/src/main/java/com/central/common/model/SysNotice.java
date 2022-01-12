package com.central.common.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 公告实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_notice")
@ApiModel("公告实体")
public class SysNotice extends SuperEntity {
    private static final long serialVersionUID = -5886012896705137070L;

	@ApiModelProperty(value = "公告内容")
	private String content;

	@ApiModelProperty(value = "备注")
	private String remarks;
	@ApiModelProperty(value = "类型(1:一般,2:维护,3:系统)")
	private Integer type;

	@ApiModelProperty(value = "发送方式(0:即时,1:定时)")
	private Boolean sendingMode;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "发送时间")
	private Date sendingTime;

	@ApiModelProperty(value = "状态(0:停用,1:启用)")
	private Boolean state;
}
