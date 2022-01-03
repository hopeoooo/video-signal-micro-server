package com.central.platform.backend.system.controller;

import com.central.common.constant.CommonConstant;
import com.central.common.feign.UserService;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysPlatformConfig;
import com.central.common.model.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    @Resource
    private UserService userService;

    @ApiOperation(value = "查询列表")
    @GetMapping("/list")
    public String list(){
        return "test.game";
    }



    /**
     * 全局参数:游客管理查询
     */
    @ApiOperation(value = "全局参数:游客管理查询")
    @GetMapping("/findTouristAmount")
    public SysPlatformConfig findTouristAmount() {
        return  userService.findTouristAmount();
    }


    /**
     * 全局参数:游客管理编辑
     * @param params
     * @return
     */
    @ApiOperation(value = "全局参数:游客管理编辑")
    @PostMapping("/saveTourist")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "touristAmount", value = "游客携带金额", required = false),
            @ApiImplicitParam(name = "touristSingleMaxBet", value = "游客单笔最大投注", required = false)
    })
    public Result saveTourist(@RequestParam Map<String, String> params) {
        return userService.saveTourist(params);
    }
}
