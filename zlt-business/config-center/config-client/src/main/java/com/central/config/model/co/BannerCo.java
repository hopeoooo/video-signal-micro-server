package com.central.config.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class BannerCo {

    @ApiModelProperty(value = "语言，0：中文，1：英文,2：柬埔寨语，3：泰语")
    private Integer languageType;

}
