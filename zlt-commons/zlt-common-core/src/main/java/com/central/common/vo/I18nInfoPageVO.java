package com.central.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("国际化字典分页VO")
@Data
public class I18nInfoPageVO {

    @ApiModelProperty("页面")
    private String page;

    @ApiModelProperty("位置")
    private String position;

    @ApiModelProperty("中文")
    private String zhCn;

    @ApiModelProperty("英文")
    private String enUs;

    @ApiModelProperty("高棉语")
    private String khm;

    @ApiModelProperty("泰文")
    private String th;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("更新日期")
    private Date updateTime;

}
