package com.central.config.controller;

import com.central.common.model.PageResult2;
import com.central.common.model.Result;
import com.central.config.model.DownloadStation;
import com.central.config.service.IDownloadStationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer")
    })
    public PageResult2<DownloadStation> findDownloadStationList(@RequestParam Map<String, Object> params) {
        return downloadStationService.findDownloadStationList(params);
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
