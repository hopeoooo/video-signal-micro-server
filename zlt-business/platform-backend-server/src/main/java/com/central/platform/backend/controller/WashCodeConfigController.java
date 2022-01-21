package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.config.feign.ConfigService;
import com.central.config.model.WashCodeConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = "洗码配置")
@Slf4j
@RequestMapping("/washCode/backend")
public class WashCodeConfigController {
    @Resource
    private ConfigService configService;

    @ApiOperation("查询洗码配置")
    @ResponseBody
    @GetMapping("/washCode/findWashCodeConfigList")
    public Result<List<WashCodeConfig>> findWashCodeConfigList() {
        return configService.findWashCodeConfigList();
    }
}
