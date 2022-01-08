package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.common.model.SysBanner;
import com.central.config.dto.TouristDto;
import com.central.config.feign.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 轮播图
 */
@Slf4j
@RestController
@Api(tags = "轮播图")
@RequestMapping("/platform/backend")
public class SysBannerController {
    @Resource
    private ConfigService configService;


    /**
     * 查询banner列表
     */
    @ApiOperation("查询banner列表")
    @ResponseBody
    @GetMapping("/banner/findBannerList")
    public Result findBannerList() {
        return configService.findBannerList();

    }

    /**
     * 删除
     *
     * @param id
     */
    @ApiOperation("删除banner")
    @DeleteMapping(value = "/banner/delBannerId/{id}")
    public Result delBannerId(@PathVariable Long id) {
        return configService.delBannerId(id);
    }

    /**
     * 修改banner状态
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "修改banner状态")
    @GetMapping("/banner/updateState")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "state", value = "状态", required = true, dataType = "Boolean")
    })
    public Result updateState(@RequestParam Map<String, Object> params) {
        return configService.updateState(params);
    }

    /**
     * 新增or更新
     *
     * @param sysBanner
     * @return
     */
    @ApiOperation(value = "新增or更新banner")
    @PostMapping("/banner/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SysBanner sysBanner) throws Exception {
        return configService.saveOrUpdate(sysBanner);
    }

}
