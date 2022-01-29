package com.central.common.params.translate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

@ApiModel
@Data
public class UpdateI18nInfoParam {

    @ApiModelProperty(value = "主键", required = true)
    private Long id;

    @ApiModelProperty(value = "页面id")
    private Long pageId;

    @ApiModelProperty(value = "位置Id")
    private Long positionId;

    @ApiModelProperty(value = "中文")
    private String zhCn;

    @ApiModelProperty(value = "英文")
    private String enUs;

    @ApiModelProperty(value = "高棉语")
    private String khm;

    @ApiModelProperty(value = "泰文")
    private String th;

    String operator;
}
