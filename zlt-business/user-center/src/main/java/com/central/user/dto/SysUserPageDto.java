package com.central.user.dto;

import com.central.common.model.SuperPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户列表查询")
public class SysUserPageDto extends SuperPage {

}
