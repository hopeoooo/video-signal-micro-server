package com.central.user.controller;

import com.central.common.model.Result;
import com.central.common.model.SysPlatformConfig;
import com.central.user.service.ISysPlatformConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;


@Slf4j
@RestController
@Api(tags = "系统配置")
@RequestMapping("/system")
public class SysPlatformConfigController {
    @Autowired
    private ISysPlatformConfigService sysPlatformConfigService;

    @ApiOperation(value = "查询游客管理")
    @GetMapping("/findTouristAmount")
    public SysPlatformConfig findTouristAmount() {
        return sysPlatformConfigService.findTouristAmount();
    }

    @ApiOperation(value = "保存")
    @PostMapping("/saveTourist")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "touristAmount", value = "游客携带金额", required = false),
            @ApiImplicitParam(name = "touristSingleMaxBet", value = "游客单笔最大投注", required = false)
    })
    public Result saveTourist(@RequestParam Map<String, String> params) {
        BigDecimal touristAmount =new BigDecimal( params.get("touristAmount"));
        BigDecimal touristSingleMaxBet =new BigDecimal(  params.get("touristSingleMaxBet"));
        return sysPlatformConfigService.saveCache(touristAmount,touristSingleMaxBet);
    }
}
