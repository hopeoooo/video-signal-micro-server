package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.config.feign.ConfigService;
import com.central.config.model.DownloadStation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@Api(tags = "app下载地址配置")
@RequestMapping("/download/backend")
public class DownloadStationController {
    @Resource
    private ConfigService configService;

    /**
     * 查询app升级管理列表
     * @return
     */
    @ApiOperation("查询app升级管理列表")
    @ResponseBody
    @GetMapping("/download/findDownloadStationList")
    public Result findDownloadStationList() {
       return configService.findDownloadStationList();
    }


    /**
     * 新增or更新
     *
     * @param downloadStation
     * @return
     */
    @ApiOperation(value = "新增or更新App升级管理")
    @PostMapping("/download/saveOrUpdateDownloadStation")
    public Result saveOrUpdateDownloadStation(@RequestBody DownloadStation downloadStation) throws Exception {
        return configService.saveOrUpdateDownloadStation(downloadStation);
    }

}
