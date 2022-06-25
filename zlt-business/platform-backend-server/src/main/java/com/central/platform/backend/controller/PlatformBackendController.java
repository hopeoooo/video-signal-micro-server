package com.central.platform.backend.controller;

import com.central.config.dto.BetMultipleDto;
import com.central.config.dto.TouristDto;
import com.central.config.feign.ConfigService;
import com.central.common.model.Result;
import com.central.config.model.co.BetMultipleCo;
import com.central.config.model.co.SaveTouristCo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * 平台后端管理系统服
 */
@Slf4j
@RestController
@Api(tags = "平台后端管理系统服api")
@RequestMapping("/platform/backend")
public class PlatformBackendController {
    @Resource
    private ConfigService configService;


    @ApiOperation(value = "查询列表")
    @GetMapping("/list")
    public String list(){
        return "test.game-dev1";
    }


    /**
     * 全局参数:游客管理查询
     */
    @ApiOperation(value = "全局参数:游客管理查询")
    @GetMapping("/system/findTouristAmount")
    public Result<TouristDto> findTouristAmount() {
        return configService.findTouristAmount();
    }


    /**
     * 全局参数:游客管理编辑
     * @param params
     * @return
     */
    @ApiOperation(value = "全局参数:游客管理编辑")
    @PostMapping("/system/saveTourist")
    public Result saveTourist(@ModelAttribute SaveTouristCo params) {
        return configService.saveTourist(params);
    }

    /**
     * 金钱符号查询
     * @return
     */
    @ApiOperation("金钱符号查询")
    @GetMapping("/system/findMoneySymbol")
    public Result findMoneySymbol(){
        return Result.succeed(configService.findMoneySymbol(), "查询成功");
    }


    /**
     * 修改金钱符号
     * @return
     */

    @ApiOperation("编辑金钱符号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moneySymbol", value = "金钱符号", required = true),
    })
    @PostMapping("/system/updateMoneySymbol")
    public Result updateMoneySymbol(@RequestParam("moneySymbol")String moneySymbol){
        return configService.updateMoneySymbol(moneySymbol);
    }


    /**
     * logo查询
     * @return
     */
    @ApiOperation("logo查询")
    @GetMapping("/system/findLogoUrlInfo")
    public Result findLogoUrlInfo(){
        return configService.findLogoUrlInfo();
    }

    /**
     * 编辑logo图
     * @param file
     * @return
     * @throws Exception
     */
    @ApiOperation("编辑logo图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "logo标识(0:网站icon,1:pc,2:app,3:app登录注册页)", required = true),
    })
    @PostMapping(value = "/saveLogoPicturePc",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result saveLogoPicturePc(@RequestPart(value = "file", required = true) MultipartFile file,@RequestParam("type") Integer type) throws Exception {
      return  configService.saveLogoPicturePc(file,type);
    }

    /**
     * 查询头像列表
     * @return
     */
    @ApiOperation("查询头像列表")
    @GetMapping("/system/findAvatarPictureList")
    public Result findAvatarPictureList(){
        return configService.findAvatarPictureList();
    }

    /**
     * 上传头像
     * @param file
     * @return
     */
    @ApiOperation("上传头像")
    @ResponseBody
    @RequestMapping(value = "/system/saveAvatarPicture",method = {RequestMethod.POST})
    public Result saveAvatarPicture(@RequestPart(value = "file", required = true) MultipartFile[] file) {
        return  configService.saveAvatarPicture(file);
    }



    /**
     * 删除
     *
     * @param id
     */
    @ApiOperation("删除头像")
    @DeleteMapping(value = "/delAvatarPictureId/{id}")
    public Result delAvatarPictureId(@PathVariable Long id) {
        return  configService.delAvatarPictureId(id);
    }



    /**
     * 查询最低在线人数
     * @return
     */
    @ApiOperation("查询最低在线人数")
    @GetMapping("/system/findMinOnlineUserQuantity")
    public Result<String> findMinOnlineUserQuantity(){
        return configService.findMinOnlineUserQuantity();
    }



    /**
     * 修改最低在线人数
     * @return
     */
    @ApiOperation("编辑最低在线人数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "minOnlineUserQuantity", value = "最低在线人数", required = true),
    })
    @PostMapping("/system/updateMinOnlineUserQuantity")
    public Result updateMinOnlineUserQuantity(@RequestParam("minOnlineUserQuantity") String minOnlineUserQuantity){
        return configService.updateMinOnlineUserQuantity(minOnlineUserQuantity);
    }


    /**
     * 打码量预设查询
     * @return
     */
    @ApiOperation("打码预设量")
    @GetMapping("/system/findBetMultiple")
    public Result<BetMultipleDto> findBetMultiple(){
        return configService.findBetMultiple();
    }

    /**
     * 打码量预设编辑
     * @param params
     * @return
     */
    @ApiOperation(value = "全局参数:游客管理编辑")
    @PostMapping("/system/saveBetMultiple")
    public Result saveBetMultiple(@ModelAttribute BetMultipleCo params) {
        return configService.saveBetMultiple(params);
    }

}
