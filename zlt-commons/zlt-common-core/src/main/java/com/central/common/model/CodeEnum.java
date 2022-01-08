package com.central.common.model;

/**
 * 消息错误码
 */
public enum CodeEnum {
    SUCCESS(0),
    ERROR(1),
    /** 授权相关错误 */
    ERROR_AUTH(100),
    /** 安全认证失败 */
    ERROR_AUTH_SECURITY(110);

    private Integer code;
    CodeEnum(Integer code){
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
