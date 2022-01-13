package com.central.config.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@TableName("download_station")
@ApiModel("下载站")
public class DownloadStation extends SuperEntity {

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
