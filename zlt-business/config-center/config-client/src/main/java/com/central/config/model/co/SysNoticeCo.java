package com.central.config.model.co;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class SysNoticeCo {

    @ApiModelProperty(value = "公告内容")
    private String content;
    @ApiModelProperty(value = "备注")
    private String remarks;
    @ApiModelProperty(value = "类型(1:一般,2:维护,3:系统)")
    private Integer type;
    @ApiModelProperty(value = "状态(0:停用,1:启用)")
    private Boolean state;
    @ApiModelProperty(value = "语言，0：中文，1：英文,2：柬埔寨语，3：泰语")
    private Integer languageType;
    @ApiModelProperty(value = "id")
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
}
