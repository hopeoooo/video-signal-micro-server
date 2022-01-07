package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.common.model.SysPlatformConfig;
import com.central.platform.backend.dto.TouristDto;
import com.central.platform.backend.service.ISysPlatformConfigService;
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
 * 平台后端管理系统服
 */
@Slf4j
@RestController
@Api(tags = "平台后端管理系统服api")
@RequestMapping("/platform/backend")
public class PlatformBackendController {
    @Autowired
    private ISysPlatformConfigService platformConfigService;

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
    @PostMapping("/system/saveTourist")
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
}
