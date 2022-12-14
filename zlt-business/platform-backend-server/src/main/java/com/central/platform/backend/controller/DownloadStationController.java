package com.central.platform.backend.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.config.feign.ConfigService;
import com.central.config.model.DownloadStation;
import com.central.common.model.co.PageCo;
import com.central.config.model.co.DownloadStationCo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


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
    public Result<PageResult<DownloadStation>> findDownloadStationList(@ModelAttribute PageCo params) {
        PageResult<DownloadStation> downloadStationList = configService.findDownloadStationList(params);
        return Result.succeed(downloadStationList);
    }


    /**
     * 新增or更新
     *
     * @param downloadStation
     * @return
     */
    @ApiOperation(value = "新增or更新App升级管理")
    @PostMapping("/download/saveOrUpdateDownloadStation")
    public Result saveOrUpdateDownloadStation(@RequestBody DownloadStationCo downloadStation, @LoginUser SysUser user) throws Exception {
        if(downloadStation.getId()==null){
            downloadStation.setCreateBy(user.getUsername());
            downloadStation.setUpdateBy(user.getUsername());
        }else {
            downloadStation.setUpdateBy(user.getUsername());
        }
        return configService.saveOrUpdateDownloadStation(downloadStation);
    }


    /**
     * 自动生成版本号
     * @param terminalType
     * @return
     */
    @ApiOperation("自动生成版本号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terminalType", value = "终端类型,1：安卓，2：ios", required = true),
    })
    @GetMapping("/download/generateVersionNumber")
    public Result<List<String>>  generateVersionNumber( @RequestParam("terminalType")String  terminalType) {
        return configService.generateVersionNumber(terminalType);
    }



    /**
     * 删除
     *
     * @param id
     */
    @ApiOperation("删除")
    @DeleteMapping(value = "/download/deleteDownloadStationId/{id}")
    public Result deleteDownloadStationId(@PathVariable Long id) {
        return configService.deleteDownloadStationId(id);
    }

}
