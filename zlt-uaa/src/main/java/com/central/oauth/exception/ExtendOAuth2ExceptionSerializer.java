package com.central.oauth.exception;

import com.central.common.model.Result;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ExtendOAuth2ExceptionSerializer extends StdSerializer<ExtendOAuth2Exception> {

    public ExtendOAuth2ExceptionSerializer() {
        super(ExtendOAuth2Exception.class);
    }

    @Override
    public void serialize(ExtendOAuth2Exception e,JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//        System.out.println("Auth返回的状态码："+String.valueOf(e.getHttpErrorCode());
        Result result = Result.failed(e.getResp_code(), e.getMessage(), e.getData_msg());
        jsonGenerator.writeObject(result);
    }
}