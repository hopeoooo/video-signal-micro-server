package com.central.file.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.file.feign.callback.FileServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

;

/**
 */
@FeignClient(name = ServiceNameConstants.CONFIG_FLIE, fallbackFactory = FileServiceFallbackFactory.class, decode404 = true)
public interface FileService {
    /**
     * 查询文件列表
     */
    @PostMapping(value = "/flies/list")
    String list();

}