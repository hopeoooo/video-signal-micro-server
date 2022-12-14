package com.central.file.controller;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.file.model.FileInfo;
import com.central.file.service.IFileService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 文件上传
 */
@RestController
@Api(tags = "文件上传Api")
public class FileController {
    @Resource
    private IFileService fileService;

    /**
     * 文件上传
     * 根据fileType选择上传方式
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/files-anon")
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception {
        FileInfo upload = fileService.upload(file);
        return Result.succeed(upload,"操作成功");
    }

    /**
     * 文件删除
     *
     * @param id
     */
    @DeleteMapping("/files/{id}")
    public Result delete(@PathVariable String id) {
        try {
            fileService.delete(id);
            return Result.succeed("操作成功");
        } catch (Exception ex) {
            return Result.failed("操作失败");
        }
    }

    /**
     * 文件查询
     *
     * @param params
     * @return
     */
    @GetMapping("/files")
    public PageResult<FileInfo> findFiles(@RequestParam Map<String, Object> params) {
        return fileService.findList(params);
    }

    /**
     * test
     */
    @GetMapping(value = "/flies/test")
    public String test(){
        return "test";
    }
}
