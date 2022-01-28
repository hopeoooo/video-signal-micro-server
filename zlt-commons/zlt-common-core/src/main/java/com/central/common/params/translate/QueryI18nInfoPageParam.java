package com.central.common.params.translate;

import com.central.common.constant.I18nKeys;
import com.central.common.params.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("查询国际化字典分页参数")
@Data
public class QueryI18nInfoPageParam extends PageParam {

    @ApiModelProperty(required = true, value = "所属 0=前台， 1=后台， 默认1")
    private Integer from = I18nKeys.BACKEND;

    @ApiModelProperty(value = "页面id")
    private Long pageId;

    @ApiModelProperty(value = "位置Id")
    private Long positionId;

    @ApiModelProperty(value = "语言 0=中文 1=英文 2=高棉语 3=泰文")
    private Integer language;

    @ApiModelProperty(value = "查询文本")
    private String word;

}
