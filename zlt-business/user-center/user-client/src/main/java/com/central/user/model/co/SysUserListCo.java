package com.central.user.model.co;

import com.central.common.model.co.PageCo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SysUserListCo extends PageCo {

    @ApiModelProperty(value = "会员账号")
    private String username;

    @ApiModelProperty(value = "状态：0.禁用，1.启用")
    private Boolean enabled;

    @ApiModelProperty(value = "是否模糊查询(0:不勾选 1:勾选")
    private Boolean isOpen;

    private String type;

}
