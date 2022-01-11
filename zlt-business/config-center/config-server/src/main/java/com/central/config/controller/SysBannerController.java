package com.central.config.controller;

import cn.hutool.core.util.StrUtil;
import com.central.common.model.Result;
import com.central.common.model.SysBanner;
import com.central.config.service.ISysBannerService;
import com.central.file.feign.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@Api(tags = "轮播图管理")
@RequestMapping("/banner")
public class SysBannerController {

    @Autowired
    private ISysBannerService bannerService;

    @Resource
    private FileService fileService;

    /**
     * 查询公告管理列表
     */
    @ApiOperation("查询banner列表")
    @ResponseBody
    @GetMapping("/findBannerList")
    public Result findBannerList() {
        List<SysBanner> bannerList = bannerService.findBannerList();
        return Result.succeed(bannerList,"查询成功");

    }

    /**
     * 删除
     *
     * @param id
     */
    @ApiOperation("删除banner")
    @DeleteMapping(value = "/delBannerId/{id}")
    public Result delBannerId(@PathVariable Long id) {
        //查询banner是否存在
        SysBanner banner = bannerService.selectById(id);
        if (banner==null){
            return Result.failed("banner不存在");
        }
        boolean b = bannerService.delBannerId(id);
        //删除对应的图片库数据
        if (StrUtil.isNotEmpty(banner.getH5FileId())){
            fileService.delete(banner.getH5FileId());
        }
        if (StrUtil.isNotEmpty(banner.getWebFileId())){
            fileService.delete(banner.getWebFileId());
        }
        return b ? Result.succeed("删除成功") : Result.failed("删除失败");
    }

    /**
     * 上传图片
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/files-anon",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result upload(@RequestPart("file") MultipartFile file) throws Exception {
      return fileService.upload(file);
    }
    /**
     * 修改banner状态
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "修改banner状态")
    @GetMapping("/updateState")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "state", value = "状态", required = true, dataType = "Boolean")
    })
    public Result updateState(@RequestParam Map<String, Object> params) {
        int i = bannerService.updateState(params);
        return i>0 ? Result.succeed("更新成功") : Result.failed("更新失败");
    }


    /**
     * 新增or更新
     *
     * @param sysBanner
     * @return
     */
    @ApiOperation(value = "新增or更新banner")
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SysBanner sysBanner) throws Exception {
        boolean result = bannerService.saveOrUpdateUser(sysBanner);
        return result ? Result.succeed(sysBanner, "操作成功") : Result.failed("操作失败");
    }

}
