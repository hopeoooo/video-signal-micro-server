package com.central.config.controller;

import cn.hutool.core.util.StrUtil;
import com.central.common.model.Result;
import com.central.common.model.SysPlatformConfig;
import com.central.config.dto.TouristDto;
import com.central.config.dto.logoUrlDto;
import com.central.config.service.ISysPlatformConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 配置模块
 */
@Slf4j
@RestController
@Api(tags = "配置模块api")
@RequestMapping("/system")
public class ConfigController {
    @Autowired
    private ISysPlatformConfigService platformConfigService;

    @ApiOperation(value = "查询配置列表")
    @GetMapping("/list")
    public String list(){
        return "test.game";
    }

    /**
     * 全局参数:游客管理查询
     */
    @ApiOperation(value = "全局参数:游客管理查询")
    @GetMapping("/findTouristAmount")
    public Result<TouristDto> findTouristAmount() {
        SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
        TouristDto platformConfigDto=new TouristDto();
        platformConfigDto.setTouristAmount(touristAmount.getTouristAmount());
        platformConfigDto.setTouristSingleMaxBet(touristAmount.getTouristSingleMaxBet());
        return Result.succeed(platformConfigDto, "查询成功");
    }

    /**
     * 全局参数:游客管理编辑
     * @param params
     * @return
     */
    @ApiOperation(value = "全局参数:游客管理编辑")
    @PostMapping("/saveTourist")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "touristAmount", value = "游客携带金额", required = true),
            @ApiImplicitParam(name = "touristSingleMaxBet", value = "游客单笔最大投注", required = true)
    })
    public Result saveTourist(@RequestParam Map<String, String> params) {
        //校验数字
        String regex = "^[0-9]*$";
        if (!params.get("touristAmount").matches(regex) || !params.get("touristSingleMaxBet").matches(regex)) {
            return Result.failed("金额只能输入数字");
        }
        BigDecimal touristAmount =new BigDecimal( params.get("touristAmount"));
        BigDecimal touristSingleMaxBet =new BigDecimal(  params.get("touristSingleMaxBet"));
        if(touristAmount.compareTo(BigDecimal.ZERO)==-1 || touristSingleMaxBet.compareTo(BigDecimal.ZERO)==-1){
            return Result.failed("金额不能小于0");
        }
        return platformConfigService.saveCache(touristAmount,touristSingleMaxBet);
    }

    /**
     * 金钱符号查询
     * @return
     */
    @ApiOperation("金钱符号查询")
    @GetMapping("/findMoneySymbol")
    public Result<String> findMoneySymbol(){
        SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
        String moneySymbol = touristAmount.getMoneySymbol() == null ? "￥" : touristAmount.getMoneySymbol();
        return Result.succeed(moneySymbol, "查询成功");
    }


    /**
     * 修改金钱符号
     * @return
     */
    @ApiOperation("编辑金钱符号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moneySymbol", value = "金钱符号", required = true),
    })
    @PostMapping("/updateMoneySymbol")
    public Result updateMoneySymbol(@RequestParam("moneySymbol") String moneySymbol){
        if (StrUtil.isBlank(moneySymbol)){
            return Result.failed("参数错误");
        }
        SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
        if (touristAmount==null){
            return Result.failed("更新失败");
        }
        touristAmount.setMoneySymbol(moneySymbol);
        boolean save = platformConfigService.saveOrUpdate(touristAmount);
        return save  ? Result.succeed("更新成功") : Result.failed("更新失败");
    }



    /**
     * logo查询
     * @return
     */
    @ApiOperation("logo查询")
    @GetMapping("/findLogoUrlInfo")
    public Result<logoUrlDto> findLogoUrlInfo(){
        SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
        if (touristAmount==null){
            return Result.succeed( "查询失败");
        }
        logoUrlDto logoUrlDto=new logoUrlDto();
        logoUrlDto.setLogImageUrlApp(touristAmount.getLogImageUrlApp());
        logoUrlDto.setLogImageUrlPc(touristAmount.getLogImageUrlPc());
        logoUrlDto.setLoginRegisterLogImageUrlApp(touristAmount.getLoginRegisterLogImageUrlApp());
        logoUrlDto.setWebsiteIcon(touristAmount.getWebsiteIcon());
        return Result.succeed(logoUrlDto, "查询成功");
    }


}
