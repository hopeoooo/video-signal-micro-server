package com.central.file.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import com.central.file.feign.callback.FileServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 文件上传
     * 根据fileType选择上传方式
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/files/files-anon",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result upload(@RequestPart("file") MultipartFile file) throws Exception ;

}