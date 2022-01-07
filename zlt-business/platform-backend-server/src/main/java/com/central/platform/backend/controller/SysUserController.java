package com.central.platform.backend.controller;

import com.central.common.constant.CommonConstant;
import com.central.common.model.*;
import com.central.log.annotation.AuditLog;
import com.central.platform.backend.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Api(tags = "会员中心")
@Slf4j
@RequestMapping("/platform/backend")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 用户列表查询。查询APP用户数据
     */
    @ApiOperation(value = "用户列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "username", value = "会员账号", required = false, dataType = "String"),
            @ApiImplicitParam(name = "enabled", value = "状态：0.禁用，1.启用", required = false, dataType = "Boolean")
    })
    @GetMapping("/users/list")
    public Result<PageResult2<SysUser>> list(@RequestParam Map<String, Object> params) {
        params.put("type", CommonConstant.USER_TYPE_APP);//APP用户数据
        PageResult2<SysUser> sysUserList = sysUserService.findSysUserList(params);
        return Result.succeed(sysUserList);
    }



    /**
     * 新增or更新 APP用户
     *
     * @param sysUser
     * @return
     */
    @PostMapping("/users/saveOrUpdate")
    @ApiOperation(value = "新增or更新")
    @AuditLog(operation = "'新增或更新用户:' + #sysUser.username")
    public Result saveOrUpdate(@RequestBody SysUser sysUser) throws Exception {
        if(StringUtils.isBlank(sysUser.getUsername()) || !sysUser.getUsername().matches(RegexEnum.ACCOUNT.getRegex())){
            return Result.failed(RegexEnum.ACCOUNT.getName() + RegexEnum.ACCOUNT.getDesc());
        }
        sysUser.setType(CommonConstant.USER_TYPE_APP);
        return sysUserService.saveOrUpdate(sysUser);
    }

    /**
     * 删除用户
     *
     * @param id
     */
    @ApiOperation(value = "删除用户")
    @DeleteMapping(value = "/users/{id}")
    @AuditLog(operation = "'删除用户:' + #id")
    public Result delete(@PathVariable Long id) {
        return sysUserService.delete(id);
    }

    /**
     * 修改用户状态
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "修改用户状态")
    @GetMapping("/users/updateEnabled")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "enabled", value = "是否启用(状态：0.禁用，1.启用)", required = true, dataType = "Boolean")
    })
    public Result updateEnabled(@RequestParam Map<String, Object> params) {
        return sysUserService.updateEnabled(params);
    }


    /**
     * 重置密码
     */
    @ApiOperation(value = "重置密码")
    @PutMapping(value = "/users/{id}/password")
    public Result resetPasswords(@PathVariable Long id) {
        return sysUserService.resetPassword(id);
    }

}
