package com.central.config.controller;

import com.central.common.model.Result;
import com.central.config.model.WashCodeConfig;
import com.central.config.service.IWashCodeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@Api(tags = "洗码配置")
@RequestMapping("/washCode")
public class WashCodeConfigController {


    @Autowired
    private IWashCodeConfigService washCodeConfigService;


    @ApiOperation("查询洗码配置")
    @ResponseBody
    @GetMapping("/findWashCodeConfigList")
    public Result<List<WashCodeConfig>> findWashCodeConfigList() {
        List<WashCodeConfig> washCodeConfigList = washCodeConfigService.findWashCodeConfigList();
        return Result.succeed(washCodeConfigList);

    }
}
