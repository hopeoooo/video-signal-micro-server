package com.central.config.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.Result;
import com.central.config.constants.ConfigConstants;
import com.central.config.model.SysBanner;
import com.central.config.service.ISysBannerService;
import com.central.file.feign.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
    public Result<List<SysBanner>> findBannerList() {
        List<SysBanner> bannerList = bannerService.findBannerList();
        return Result.succeed(bannerList);

    }

    @ApiOperation("查询banner列表(前台用)")
    @ResponseBody
    @GetMapping("/getBannerList")
    public Result<List<SysBanner>> getBannerList() {
        LambdaQueryWrapper<SysBanner> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysBanner::getState, Boolean.TRUE);
        lqw.orderByAsc(SysBanner::getSort);
        List<SysBanner> bannerList = bannerService.list(lqw);
        return Result.succeed(bannerList);
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
     * @return
     */
    @ApiOperation(value = "新增or更新banner")
    @PostMapping(value = "/saveOrUpdate",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "linkUrl", value = "链接url", required = false),
            @ApiImplicitParam(name = "sort", value = "排序", required = true),
            @ApiImplicitParam(name = "id", value = "id", required = false),
    })
    public Result saveOrUpdate(
            @RequestPart(value = "fileH5", required = false) MultipartFile fileH5,
            @RequestPart(value = "fileWeb", required = false) MultipartFile fileWeb,Integer sort,String linkUrl, Long id
    ) throws Exception {
        SysBanner sysBanner=new SysBanner();
        if (id!=null){
            sysBanner.setId(id);
        }else {
            Integer integer = bannerService.queryTotal(null);
            if (integer==20){
                return Result.failed("最多添加20条数据");
            }
        }
        Integer  queryTotal= bannerService.queryTotal(sort);
        if (queryTotal>0){
          return   Result.failed("排序位置已经存在");
        }
        sysBanner.setSort(sort);
        if (StrUtil.isNotEmpty(linkUrl)) {
            sysBanner.setLinkUrl(linkUrl);
        }
        //图片
        if (fileH5!=null && fileH5.getSize()>0){
            //校验格式
            Boolean aBoolean = verifyFormat(fileH5.getOriginalFilename());
            if (!aBoolean){
                return Result.failed("格式错误");
            }
            //调用上传
            Map<String, String> upload = upload(fileH5);
            String url= upload.get("url");
            String fileId= upload.get("fileId");
            sysBanner.setH5Url(url);
            sysBanner.setH5FileId(fileId);
        }
        if (fileWeb!=null && fileWeb.getSize()>0){
            //校验格式
            Boolean aBoolean = verifyFormat(fileWeb.getOriginalFilename());
            if (!aBoolean){
                return Result.failed("格式错误");
            }
            //调用上传
            Map<String, String> upload = upload(fileWeb);
            String url= upload.get("url");
            String fileId= upload.get("fileId");
            sysBanner.setWebUrl(url);
            sysBanner.setWebFileId(fileId);
        }
        boolean result = bannerService.saveOrUpdateUser(sysBanner);
        return result ? Result.succeed( "操作成功") : Result.failed("操作失败");
    }


    /**
     * 上传
     * @param file
     * @return
     */
    public Map<String,String> upload(MultipartFile file){
        Map<String,String> info=new HashMap<>();
        Result upload = null;
        try {
            upload = fileService.upload(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (upload.getResp_code()==0){
            JSONObject JSONObject=new JSONObject(upload.getDatas());
            String fileId = String.valueOf(JSONObject.get("id"));
            String url=String.valueOf(JSONObject.get("url"));
            info.put("url",url);
            info.put("fileId",fileId);
        }
        return info;
    }

    /**
     * 校验格式
     * @param fileName
     * @return
     */
    public Boolean verifyFormat(String fileName){
        Boolean identification=false;
        //格式
        String substring = fileName.substring(fileName.lastIndexOf("."));
        if (substring.equals(ConfigConstants.bmp) ||substring.equals(ConfigConstants.gif)||substring.equals(ConfigConstants.jpeg)||substring.equals(ConfigConstants.jpg)){
            identification=true;
        }
        return identification;
    }

}
