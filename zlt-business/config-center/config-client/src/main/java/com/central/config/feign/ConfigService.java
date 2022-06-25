package com.central.config.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.co.PageCo;
import com.central.config.dto.BetMultipleDto;
import com.central.config.dto.TouristDto;
import com.central.config.feign.callback.ConfigServiceFallbackFactory;
import com.central.config.model.DownloadStation;
import com.central.config.model.SysBanner;
import com.central.config.model.SysNotice;
import com.central.config.model.co.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 */
@FeignClient(name = ServiceNameConstants.CONFIG_SERVICE, fallbackFactory = ConfigServiceFallbackFactory.class, decode404 = true)
public interface ConfigService {
    /**
     * 查询游戏列表
     */
    @GetMapping(value = "/system/list")
    String list();

    @GetMapping("/system/findTouristAmount")
    Result<TouristDto> findTouristAmount() ;

    @PostMapping("/system/saveTourist")
    Result saveTourist(@SpringQueryMap SaveTouristCo params) ;

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
    Result<List<SysNotice>> findNoticeList(@SpringQueryMap FindNoticeCo params) ;

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
    Result updateEnabled(@SpringQueryMap UpdateNoticeCo params) ;

    /**
     * 公告新增or修改
     * @param sysNotice
     * @return
     */
    @PostMapping("/notice/saveOrUpdate")
    Result saveOrUpdate(@RequestBody SysNoticeCo sysNotice);


    /**
     * 查询banner列表
     * @return
     */
    @GetMapping("/banner/findBannerList")
    Result<List<SysBanner>> findBannerList() ;

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
    Result updateState(@SpringQueryMap BannerUpdateStateCo params) ;

    /**
     * banner新增or修改
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/banner/saveOrUpdate",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result saveOrUpdate(
            @RequestPart(value = "fileH5", required = false) MultipartFile fileH5,
            @RequestPart(value = "fileH5Horizontal", required = false) MultipartFile fileH5Horizontal,
            @RequestPart(value = "fileWeb", required = false) MultipartFile fileWeb,
            @RequestParam(value = "sort",required = true) Integer sort,
            @RequestParam(value ="linkUrl",required = false) String linkUrl,
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
    /**
     * 查询app升级管理列表
     * @return
     */
    @GetMapping("/download/findDownloadStationList")
    PageResult<DownloadStation> findDownloadStationList(@SpringQueryMap PageCo params) ;

    /**
     * 新增or更新App升级管理
     *
     * @param downloadStation
     * @return
     */
    @PostMapping("/download/saveOrUpdateDownloadStation")
     Result saveOrUpdateDownloadStation(@RequestBody DownloadStationCo downloadStation) throws Exception ;


    @GetMapping("/download/generateVersionNumber")
     Result<List<String>> generateVersionNumber( @RequestParam("terminalType")  String  terminalType) ;


    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/download/deleteDownloadStationId/{id}")
    Result deleteDownloadStationId(@PathVariable("id") Long id) ;

    /**
     * 查询最低在线人数
     * @return
     */
    @GetMapping("/system/findMinOnlineUserQuantity")
     Result<String> findMinOnlineUserQuantity();

    /**
     * 编辑最低在线人数
     * @param minOnlineUserQuantity
     * @return
     */
    @PostMapping("/system/updateMinOnlineUserQuantity")
    Result updateMinOnlineUserQuantity(@RequestParam("minOnlineUserQuantity") String minOnlineUserQuantity);

    /**
     * 打码预设量查询
     *
     * @return
     */
    @GetMapping("/system/findBetMultiple")
    Result<BetMultipleDto> findBetMultiple();

    @PostMapping("/system/saveBetMultiple")
    Result saveBetMultiple(@SpringQueryMap BetMultipleCo params);

}