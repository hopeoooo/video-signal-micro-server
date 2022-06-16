package com.central.oauth2.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 认证配置
 *
 * @author zlt
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "zlt.uaa")
@RefreshScope
public class UaaProperties {

    /**
     * 是否开启同账号登录互踢
     * 1. 登录同应用同账号互踢
     */
    private Boolean isSingleLogin = false;
}
