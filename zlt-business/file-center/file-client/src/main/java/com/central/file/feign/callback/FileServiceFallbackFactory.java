package com.central.file.feign.callback;

import com.central.file.feign.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

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
        };
    }
}
