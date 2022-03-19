package com.central.user.model.co;

import com.central.common.model.co.PageCo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class SysTansterMoneyPageCo extends PageCo {

    @ApiModelProperty(value = "会员账号")
    private String userName;

    @ApiModelProperty(value = "会员Id")
    private Long userId;

    @ApiModelProperty(value = "1:转入,2:转出,3:派彩,4:下注,5:手动入款,6:手动出款")
    private String[] orderType;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

}
