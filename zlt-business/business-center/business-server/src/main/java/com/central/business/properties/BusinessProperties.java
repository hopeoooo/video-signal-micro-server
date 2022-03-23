package com.central.business.properties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author zlt
 * @date 2019/1/4
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "zlt.business")
@RefreshScope
@Configuration
@ApiModel("配置文件信息")
public class BusinessProperties {

    @ApiModelProperty(value = "首页根路径")
    private String baseUrl;

    @ApiModelProperty(value = "登录认证公钥")
    private String authorization;

    @ApiModelProperty(value = "商户数组")
    private String[] businessList = {};


}
