package com.central.file.feign.callback;

import com.central.common.model.Result;
import com.central.file.feign.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * 降级工场
 */
@Slf4j
public class FileServiceFallbackFactory implements FallbackFactory<FileService> {

    @Override
    public FileService create(Throwable cause) {
        return new FileService() {
            @Override
            public String list() {
                return null;
            }
            @Override
            public Result upload(MultipartFile file) throws Exception {
                log.error("upload上传异常", cause);
                return Result.failed("编辑失败");
            }

            @Override
            public Result delete(String id) {
                log.error("delete删除异常:{}", id, cause);
                return Result.failed("删除失败");
            }
        };
    }
}
