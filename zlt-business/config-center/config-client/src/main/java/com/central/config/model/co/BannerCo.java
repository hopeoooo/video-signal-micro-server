package com.central.config.model.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class BannerCo {

    @ApiModelProperty(value = "语言，zh_cn：中文，en_us：英文,khm：柬埔寨语，th：泰语")
    private String languageType;

}
