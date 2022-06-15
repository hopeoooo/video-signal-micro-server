package com.central.oauth.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class CustomConfig {

    @Value("${zlt.uaa.isSingleLogin:false}")
    private boolean isSingleLogin;


}
