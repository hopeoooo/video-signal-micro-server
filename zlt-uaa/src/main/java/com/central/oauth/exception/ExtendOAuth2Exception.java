package com.central.oauth.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@JsonSerialize(using = ExtendOAuth2ExceptionSerializer.class)
public class ExtendOAuth2Exception extends OAuth2Exception {
    @Getter
    private String data_msg;
    @Getter
    private int resp_code;

    public ExtendOAuth2Exception(String msg) {
        super(msg);
    }

    public ExtendOAuth2Exception(String msg, Throwable t) {
        super(msg, t);
    }

    public ExtendOAuth2Exception(String msg, int resp_code, String data_msg) {
        super(msg);
        this.resp_code = resp_code;
        this.data_msg = data_msg;
    }
}

