package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.config.feign.ConfigService;
import com.central.config.model.SysBanner;
import com.central.config.model.co.BannerUpdateStateCo;
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
import javax.validation.Valid;
import java.util.List;
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
    public Result<List<SysBanner>> findBannerList() {
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
    public Result updateState(@Valid @ModelAttribute BannerUpdateStateCo params) {
        return configService.updateState(params);
    }

    /**
     * 新增or更新
     *
     * @return
     */
    @ApiOperation(value = "新增or更新banner")
    @PostMapping(value = "/banner/saveOrUpdate",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "linkUrl", value = "链接url", required = false),
            @ApiImplicitParam(name = "sort", value = "排序", required = true),
            @ApiImplicitParam(name = "id", value = "id", required = false),
    })
    public Result saveOrUpdate(
            @RequestPart(value = "fileH5", required = false) MultipartFile fileH5,
            @RequestPart(value = "fileH5Horizontal", required = false) MultipartFile fileH5Horizontal,
            @RequestPart(value = "fileWeb", required = false) MultipartFile fileWeb,Integer sort,String linkUrl,Long id
    ) throws Exception {
        return configService.saveOrUpdate(fileH5,fileH5Horizontal,fileWeb,sort,linkUrl,id);
    }
}
