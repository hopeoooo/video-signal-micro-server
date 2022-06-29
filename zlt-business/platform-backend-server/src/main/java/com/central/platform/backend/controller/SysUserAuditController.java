package com.central.platform.backend.controller;

import com.central.common.constant.CommonConstant;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.log.annotation.AuditLog;
import com.central.user.feign.UserService;
import com.central.user.model.co.AddUserAuditCo;
import com.central.user.model.co.SysTansterMoneyPageCo;
import com.central.user.model.co.SysUserAuditPageCo;
import com.central.user.model.co.SysUserCo;
import com.central.user.model.vo.SysUserAuditVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@RestController
@Api(tags = "用户稽核记录")
@RequestMapping("/sysUserAudit")
public class SysUserAuditController {


    @Resource
    private UserService userService;

    @ApiOperation(value = "稽核列表")
    @GetMapping("/audit/list")
    public Result<PageResult<SysUserAuditVo>> list(@ModelAttribute SysUserAuditPageCo params) {
        PageResult<SysUserAuditVo> sysUserList = userService.findUserAuditList(params);
        return Result.succeed(sysUserList);
    }


    @PostMapping("/audit/addAudit")
    @ApiOperation(value = "增加打码量")
    public Result addAudit(@ModelAttribute AddUserAuditCo addUserAuditCo) {
        if(addUserAuditCo == null || addUserAuditCo.getBetAmount().compareTo(BigDecimal.ZERO) <= 0){
            return Result.failed("参数不合法");
        }
        userService.addAudit(addUserAuditCo);

        return Result.succeed();
    }

    @PostMapping("/audit/subtractAudit")
    @ApiOperation(value = "解锁打码量")
    public Result subtractAudit(@ModelAttribute AddUserAuditCo addUserAuditCo) {

        userService.subtractAudit(addUserAuditCo);

        return Result.succeed();
    }
}
