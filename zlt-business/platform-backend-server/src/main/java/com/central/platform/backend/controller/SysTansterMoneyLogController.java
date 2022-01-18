package com.central.platform.backend.controller;

import com.central.common.constant.CommonConstant;
import com.central.common.model.PageResult2;
import com.central.common.model.Result;
import com.central.common.model.SysTansterMoneyLog;
import com.central.common.model.SysUser;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.platform.backend.service.SysTansterMoneyLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@Api(tags = "用户账变记录")
@RequestMapping("/tansterMoney")
public class SysTansterMoneyLogController {

    @Autowired
    private SysTansterMoneyLogService sysTansterMoneyLogService;

    @ApiOperation(value = "用户列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "usernName", value = "会员账号", required = false, dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "会员Id", required = false, dataType = "Long"),
            @ApiImplicitParam(name = "orderType", value = "1:转入,2:转出,3:派彩,4:下注,5:手动入款,6:手动出款", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "steartTime", value = "开始时间", required = false, dataType = "Date"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "Date"),
    })
    @GetMapping("/users/list")
    public Result<PageResult2<SysTansterMoneyLogVo>> list(@RequestParam Map<String, Object> params) {
        PageResult2<SysTansterMoneyLogVo> sysUserList = sysTansterMoneyLogService.findList(params);
        return Result.succeed(sysUserList);
    }

}