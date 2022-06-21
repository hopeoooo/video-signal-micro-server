package com.central.translate.model.co;

import com.central.translate.validator.INotBlank;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class SaveI18nInfoCo {

    @ApiModelProperty(value = "页面id")
    @NotNull(message = "页面Id不能为空", groups = {Save.class})
    private Long pageId;

    @ApiModelProperty(value = "位置Id")
    private Long positionId;

    @ApiModelProperty(value = "中文")
    @INotBlank(message = "必须指定中文", groups = {Save.class, Update.class})
    private String zhCn;

    @ApiModelProperty(value = "英文")
    @NotNull(message = "英文不能为空", groups = {Save.class})
    private String enUs;

    @ApiModelProperty(value = "高棉语")
    @NotNull(message = "高棉语不能为空", groups = {Save.class})
    private String khm;

    @ApiModelProperty(value = "泰文")
    @NotNull(message = "泰文不能为空", groups = {Save.class})
    private String th;

    @ApiModelProperty(value = "所属 0=前台PC，1=后台 2=前台移动端 3=前台错误消息")
    private Integer fromOf;

    String operator;

    public interface Save {}

    public interface  Update {}

}
