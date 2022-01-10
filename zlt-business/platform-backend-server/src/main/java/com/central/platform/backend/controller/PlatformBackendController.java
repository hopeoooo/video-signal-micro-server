package com.central.platform.backend.controller;

import com.central.common.model.SysBanner;
import com.central.config.dto.TouristDto;
import com.central.config.feign.ConfigService;
import com.central.common.model.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "touristAmount", value = "游客携带金额", required = true),
            @ApiImplicitParam(name = "touristSingleMaxBet", value = "游客单笔最大投注", required = true)
    })
    public Result saveTourist(@RequestParam Map<String, String> params) {
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
     * 查询头像列表
     * @return
     */
    @ApiOperation("查询头像列表")
    @GetMapping("/system/findAvatarPictureList")
    public Result findAvatarPictureList(){
        return configService.findAvatarPictureList();
    }
}
