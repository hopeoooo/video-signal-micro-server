package com.central.oauth.exception;

import org.springframework.http.HttpStatus;

/**
 * 自定义OAuth2扩展异常
 * 返回客户端状态码为 200
 */
public class CustomExtendOAuth2Exception extends ExtendOAuth2Exception{

    public CustomExtendOAuth2Exception(String msg, Throwable t) {
        super(msg, t);
    }

//    @Override
//    public String getOAuth2ErrorCode() {
//        return "server_error---服务器内部异常"; // invalid_request
//    }

    /**
     * 返回hpptstatus为200
     * @return
     */
    @Override
    public int getHttpErrorCode() {
        return HttpStatus.OK.value();
    }

}