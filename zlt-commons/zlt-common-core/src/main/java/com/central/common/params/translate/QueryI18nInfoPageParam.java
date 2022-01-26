package com.central.common.params.translate;

import com.central.common.params.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("查询国际化字典分页参数")
@Data
public class QueryI18nInfoPageParam extends PageParam {

    @ApiModelProperty("页面id")
    private Long pageId;

    @ApiModelProperty("位置Id")
    private Long positionId;

    @ApiModelProperty("语言 0=中文 1=英文 2=高棉语 3=泰文")
    private Integer language;

    @ApiModelProperty("查询文本")
    private String word;

}
