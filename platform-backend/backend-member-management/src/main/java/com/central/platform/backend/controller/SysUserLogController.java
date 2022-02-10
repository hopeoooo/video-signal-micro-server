package com.central.platform.backend.controller;

import com.central.common.dto.LoginLogPageDto;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.user.feign.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@Api(tags = "会员中心")
@Slf4j
@RequestMapping("/userLoginLog/backend")
public class SysUserLogController {

    @Resource
    private UserService userService;

    /**
     * 查询会员登录日志列表
     */
    @ResponseBody
    @ApiOperation(value = "查询会员登录日志列表")
    @GetMapping("/loginLog/findUserLoginLogList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "userName", value = "用户帐号", required = false, dataType = "String"),
            @ApiImplicitParam(name = "loginIp", value = "登录IP", required = false, dataType = "String"),
            @ApiImplicitParam(name = "isOpen", value = "是否模糊查询(0:不勾选 1:勾选)", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "enabled", value = "状态：0.禁用，1.启用", required = false, dataType = "Integer")
    })
    public Result<PageResult<LoginLogPageDto>> findUserLoginLogList(@RequestParam Map<String, Object> params) {
        PageResult<LoginLogPageDto> userLoginLogList = userService.findUserLoginLogList(params);
        return Result.succeed(userLoginLogList);
    }
}
