package com.central.platform.backend.controller;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.user.feign.UserService;
import com.central.user.model.co.SysTansterMoneyPageCo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@Api(tags = "用户账变记录")
@RequestMapping("/tansterMoney")
public class SysTansterMoneyLogController {
    @Resource
    private UserService userService;

    @ApiOperation(value = "用户列表查询")
    @GetMapping("/users/list")
    public Result<PageResult<SysTansterMoneyLogVo>> list(@Valid @ModelAttribute SysTansterMoneyPageCo params) {
        PageResult<SysTansterMoneyLogVo> sysUserList = userService.findTransterMoneyList(params);
        return Result.succeed(sysUserList);
    }

}
