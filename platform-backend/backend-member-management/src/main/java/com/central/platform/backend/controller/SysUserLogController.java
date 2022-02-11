package com.central.platform.backend.controller;

import com.central.common.dto.LoginLogPageDto;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.user.feign.UserService;
import com.central.user.model.co.UserLoginLogPageCo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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
    public Result<PageResult<LoginLogPageDto>> findUserLoginLogList(@Valid  @ModelAttribute UserLoginLogPageCo params) {
        PageResult<LoginLogPageDto> userLoginLogList = userService.findUserLoginLogList(params);
        return Result.succeed(userLoginLogList);
    }
}
