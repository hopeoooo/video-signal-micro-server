package com.central.file.config;

import com.central.common.exception.DefaultExceptionAdvice;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionAdvice extends DefaultExceptionAdvice {

//    @ResponseStatus(HttpStatus.OK)
//    @ExceptionHandler({AmazonClientException.class})
//    public Result amazonClientException(AmazonClientException e) {
//        return super.defHandler("上传或下载失败", e);
//    }

}
