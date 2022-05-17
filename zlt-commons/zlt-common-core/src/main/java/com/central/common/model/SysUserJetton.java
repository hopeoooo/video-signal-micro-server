package com.central.common.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("筹码配置实体")
@TableName("sys_user_jetton")
public class SysUserJetton extends SuperEntity {

    @ApiModelProperty(value = "用户ID")
    private Long uid;

    @ApiModelProperty(value = "筹码配置内容")
    private String jettonConfig;
}
