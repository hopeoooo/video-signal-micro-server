package com.central.config.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.Result;
import com.central.config.dto.logoUrlDto;
import com.central.config.model.DownloadStation;
import com.central.config.model.SysPlatformConfig;
import com.central.config.service.IDownloadStationService;
import com.central.config.service.ISysPlatformConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@Api(tags = "对外接口(不认证)")
@RequestMapping("/api")
public class ApiController {
    @Autowired
    private IDownloadStationService downloadStationService;
    @Autowired
    private ISysPlatformConfigService platformConfigService;

    @Value("${zlt.app.version:1.0.0}")
    private String version;

    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String nacosNameSpace;
    @Value("${spring.cloud.nacos.server-addr}")
    private String nacosIp;
    @Value("${spring.datasource.url}")
    private String mysqlIp;
    @Value("${spring.redis.host}")
    private String redisIp;

    @ApiOperation(value = "查询版本号")
    @GetMapping("/getVersion")
    public Result<String> getVersion() {
        return Result.succeed(version, "查询成功");
    }

    @ApiOperation(value = "ip测试")
    @GetMapping("/ipTest")
    public Result ipTest() {
        Map<String,Object> map=new HashMap<>();
        map.put("nacosNameSpace",nacosNameSpace);
        map.put("nacosIp",nacosIp);
        map.put("mysqlIp",mysqlIp);
        map.put("redisIp",redisIp);
        return Result.succeed(map);
    }

    @ApiOperation("logo查询")
    @GetMapping("/findLogoUrlInfo")
    public Result<logoUrlDto> findLogoUrlInfo() {
        SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
        if (touristAmount == null) {
            return Result.succeed("查询成功");
        }
        logoUrlDto logoUrlDto = new logoUrlDto();
        logoUrlDto.setLogImageUrlApp(touristAmount.getLogImageUrlApp());
        logoUrlDto.setLogImageUrlPc(touristAmount.getLogImageUrlPc());
        logoUrlDto.setLoginRegisterLogImageUrlApp(touristAmount.getLoginRegisterLogImageUrlApp());
        logoUrlDto.setWebsiteIcon(touristAmount.getWebsiteIcon());
        return Result.succeed(logoUrlDto, "查询成功");
    }

    @ApiOperation("查询app下载地址")
    @ResponseBody
    @GetMapping("/findAppDownUrl")
    public Result<AppDown> findAppDownUrl() {
        LambdaQueryWrapper<DownloadStation> lqw = Wrappers.lambdaQuery();
        lqw.eq(DownloadStation::getTerminalType, 1);
        lqw.orderByDesc(DownloadStation::getVersionNumber);
        DownloadStation android = downloadStationService.getOne(lqw, false);

        LambdaQueryWrapper<DownloadStation> lqw1 = Wrappers.lambdaQuery();
        lqw1.eq(DownloadStation::getTerminalType, 2);
        lqw1.orderByDesc(DownloadStation::getVersionNumber);
        DownloadStation ios = downloadStationService.getOne(lqw1, false);

        AppDown appDown = new AppDown();
        appDown.setAndroid(android == null ? null : android.getDownloadUrl());
        appDown.setIos(ios == null ? null : ios.getDownloadUrl());
        return Result.succeed(appDown, "查询成功");
    }

    @ApiModel("app下载地址")
    @Data
    class AppDown {
        @ApiModelProperty(value = "安卓端下载地址")
        private String android;
        @ApiModelProperty(value = "ios端下载地址")
        private String ios;
    }
}
