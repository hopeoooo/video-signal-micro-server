package com.central.config.controller;

import com.central.common.model.Result;
import com.central.config.model.DownloadStation;
import com.central.config.service.IDownloadStationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@Api(tags = "下载站")
@RequestMapping("/download")
public class DownloadStationController {
    @Autowired
    private IDownloadStationService downloadStationService;


    /**
     * 查询app升级管理列表
     * @return
     */
    @ApiOperation("查询app升级管理列表")
    @ResponseBody
    @GetMapping("/findDownloadStationList")
    public Result findDownloadStationList() {
        List<DownloadStation> downloadStationList = downloadStationService.findDownloadStationList();
        return Result.succeed(downloadStationList,"查询成功");
    }


    /**
     * 新增or更新
     *
     * @param downloadStation
     * @return
     */
    @ApiOperation(value = "新增or更新App升级管理")
    @PostMapping("/saveOrUpdateDownloadStation")
    public Result saveOrUpdateDownloadStation(@RequestBody DownloadStation downloadStation) throws Exception {
        Result result = downloadStationService.saveOrUpdateDownloadStation(downloadStation);
        return result;
    }

}
