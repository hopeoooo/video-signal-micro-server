package com.central.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@ApiModel("国际化资源DTO")
@Data
public class I18nSourceDTO {

    @ApiModelProperty("中文国际化资源")
    private Map<String, String> zhCn;

    @ApiModelProperty("英文国际化资源")
    private Map<String, String> enUs;

    @ApiModelProperty("高棉语国际化资源")
    private Map<String, String> khm;

    @ApiModelProperty("泰语国际化资源")
    private Map<String, String> th;

}
