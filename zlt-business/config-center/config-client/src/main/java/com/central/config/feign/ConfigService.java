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



    @GetMapping("/notice/findNoticeList")
    Result findNoticeList(@RequestParam("params") Map<String, Object> params) ;

    @DeleteMapping(value = "/notice/deleteNoticeId/{id}")
     Result deleteNoticeId(@PathVariable("id") Long id);


    @GetMapping("/notice/updateEnabled")
    Result updateEnabled(@RequestParam("params") Map<String, Object> params) ;

    @PostMapping("/notice/saveOrUpdate")
    Result saveOrUpdate(@RequestBody SysNotice sysNotice);


    @GetMapping("/banner/findBannerList")
    Result findBannerList() ;

    @DeleteMapping(value = "/banner/delBannerId/{id}")
    Result delBannerId(@PathVariable("id") Long id) ;

    @GetMapping("/banner/updateState")
    Result updateState(@RequestParam("params") Map<String, Object> params) ;

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

    @GetMapping("/system/findAvatarPictureList")
     Result findAvatarPictureList() ;


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

    @DeleteMapping(value = "/system/delAvatarPictureId/{id}")
     Result delAvatarPictureId(@PathVariable("id") Long id) ;


    @PostMapping(value = "/banner/files-anon",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result upload(@RequestPart("file") MultipartFile file) throws Exception ;


    /**
     * 查询默认头像
     */
    @GetMapping("/system/avatarPictureInfo")
     String avatarPictureInfo() ;

}