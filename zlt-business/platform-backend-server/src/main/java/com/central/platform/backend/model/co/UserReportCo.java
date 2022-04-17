package com.central.platform.backend.model.co;

import com.central.common.model.co.PageCo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UserReportCo{

    @ApiModelProperty(value = "会员账号")
    private String username;

    @ApiModelProperty(value = "查询起始时间(yyyy-MM-dd)")
    private String startTime;

    @ApiModelProperty(value = "查询结束时间(yyyy-MM-dd)")
    private String endTime;

    @ApiModelProperty(value = "当前页(默认第一页)", required = true)
    @NotNull(message = "当前页不能为空")
    private Integer pageCode;

    @ApiModelProperty(value = "每页大小(默认10条)", required = true)
    @NotNull(message = "每页大小不能为空")
    private Integer pageSize;
}

