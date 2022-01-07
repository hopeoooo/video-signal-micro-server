package com.central.user.controller;

import com.central.common.dto.LoginLogPageDto;
import com.central.common.model.LoginLog;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.user.service.ILoginLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@RestController
@Api(tags = "会员登录日志")
@RequestMapping("/loginLog")
public class LoginLogController {
    @Autowired
    private ILoginLogService ILoginLogService;


    /**
     * 查询会员登录日志列表
     */
    @ResponseBody
    @GetMapping("/findUserLoginLogList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "userName", value = "用户帐号", required = false, dataType = "String"),
            @ApiImplicitParam(name = "loginIp", value = "登录IP", required = false, dataType = "String"),
            @ApiImplicitParam(name = "isOpen", value = "是否模糊查询(0:不勾选 1:勾选)", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "enabled", value = "状态：0.禁用，1.启用", required = false, dataType = "Integer")
    })
    public PageResult<LoginLogPageDto> findUserLoginLogList(@RequestParam Map<String, Object> params) {
        return ILoginLogService.queryList(params);
    }

    @PostMapping("/addLog")
    public Result<Boolean> addLoginlog(@RequestBody LoginLog loginLog){
        Boolean result = ILoginLogService.save(loginLog);
        return result? Result.succeed(Boolean.TRUE):Result.succeed(Boolean.FALSE);
    }

}
