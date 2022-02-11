package com.central.config.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class DownloadStationCo {

    private Long id;

    @ApiModelProperty(value = "版本名称")
    private String name;

    @ApiModelProperty(value = "下载地址")
    private String downloadUrl;

    @ApiModelProperty(value = "版本号")
    private String versionNumber;

    @ApiModelProperty(value = "终端类型,1：安卓，2：ios")
    private Integer terminalType;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "强制更新,1：是，2：否")
    private Integer isForced;


    private String updateBy;

    private String createBy;

}
