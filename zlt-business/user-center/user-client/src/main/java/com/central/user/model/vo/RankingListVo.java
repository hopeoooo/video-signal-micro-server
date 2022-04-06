package com.central.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("今日排行")
@Data
public class RankingListVo {

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "金额")
    private String money;

}
