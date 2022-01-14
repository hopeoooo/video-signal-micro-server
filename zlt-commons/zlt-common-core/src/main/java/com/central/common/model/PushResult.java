package com.central.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: zlt
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushResult<T> implements Serializable {

    private T datas;
    private Integer resp_code;
    private String resp_msg;
    private String type;

    public static <T> PushResult<T> succeed(T model, String type) {
        return of(model, CodeEnum.SUCCESS.getCode(), type);
    }

    public static <T> PushResult<T> of(T datas, Integer code, String type) {
        return new PushResult<>(datas, code, "消息推送成功", type);
    }
}
