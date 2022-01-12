package com.central.config.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import com.central.common.model.SysNotice;
import com.central.config.dto.TouristDto;
import com.central.config.feign.callback.ConfigServiceFallbackFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 */
@FeignClient(name = ServiceNameConstants.CONFIG_SERVICE, fallbackFactory = ConfigServiceFallbackFactory.class, decode404 = true)
public interface ConfigService {
    /**
     * 查询游戏列表
     */
    @PostMapping(value = "/system/list")
    String list();

    @GetMapping("/system/findTouristAmount")
    Result<TouristDto> findTouristAmount() ;

    @PostMapping("/system/saveTourist")
    Result saveTourist(@RequestParam("params") Map<String, String> params) ;

    @GetMapping("/system/findMoneySymbol")
    Result findMoneySymbol();

    @PostMapping("/system/updateMoneySymbol")
    Result updateMoneySymbol( @RequestParam("moneySymbol")String moneySymbol);

    @GetMapping("/system/findLogoUrlInfo")
    Result findLogoUrlInfo();


    /**
     * 查询公告列表
     * @param params
     * @return
     */
    @GetMapping("/notice/findNoticeList")
    Result findNoticeList(@RequestParam("params") Map<String, Object> params) ;

    /**
     * 删除公告
     * @param id
     * @return
     */
    @DeleteMapping(value = "/notice/deleteNoticeId/{id}")
    Result deleteNoticeId(@PathVariable("id") Long id);


    /**
     * 修改公告状态
     * @param params
     * @return
     */
    @GetMapping("/notice/updateEnabled")
    Result updateEnabled(@RequestParam("params") Map<String, Object> params) ;

    /**
     * 公告新增or修改
     * @param sysNotice
     * @return
     */
    @PostMapping("/notice/saveOrUpdate")
    Result saveOrUpdate(@RequestBody SysNotice sysNotice);


    /**
     * 查询banner列表
     * @return
     */
    @GetMapping("/banner/findBannerList")
    Result findBannerList() ;

    /**
     * 删除banner
     * @param id
     * @return
     */
    @DeleteMapping(value = "/banner/delBannerId/{id}")
    Result delBannerId(@PathVariable("id") Long id) ;

    /**
     * 修改banner状态
     * @param params
     * @return
     */
    @GetMapping("/banner/updateState")
    Result updateState(@RequestParam("params") Map<String, Object> params) ;

    /**
     * banner新增or修改
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/banner/saveOrUpdate",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result saveOrUpdate(
            @RequestPart(value = "fileH5", required = false) MultipartFile fileH5,
            @RequestPart(value = "fileWeb", required = false) MultipartFile fileWeb,
            @RequestParam(value = "sort",required = true) Integer sort,
            @RequestParam(value ="linkUrl",required = false) String linkUrl,
            @RequestParam(value = "startTime",required = false) String startTime,
            @RequestParam(value = "endTime",required = false)  String endTime,
            @RequestParam(value = "startMode",required = true)  Integer startMode,
            @RequestParam(value = "endMode",required = true) Integer endMode,
            @RequestParam(value = "id",required = false) Long id
    ) throws Exception ;


    /**
     * 编辑logo图
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/system/saveLogoPicturePc",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result saveLogoPicturePc(@RequestPart(value = "file", required = true) MultipartFile file,@RequestParam("type") Integer type) throws Exception ;


    /**
     * 上传头像
     * @param file
     * @return
     */
    @RequestMapping(value = "/system/saveAvatarPicture",method = {RequestMethod.POST},consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result saveAvatarPicture(@RequestPart(value = "file", required = true) MultipartFile[] file) ;


    /**
     * 查询头像列表
     * @return
     */
    @GetMapping("/system/findAvatarPictureList")
    Result findAvatarPictureList() ;


    /**
     * 删除头像
     * @param id
     * @return
     */
    @DeleteMapping(value = "/system/delAvatarPictureId/{id}")
    Result delAvatarPictureId(@PathVariable("id") Long id) ;


    /**
     * 查询默认头像
     */
    @GetMapping("/system/avatarPictureInfo")
    String avatarPictureInfo() ;

}