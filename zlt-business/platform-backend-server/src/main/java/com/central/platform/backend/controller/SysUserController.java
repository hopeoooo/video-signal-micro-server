package com.central.platform.backend.controller;

import com.central.common.constant.CommonConstant;
import com.central.common.model.PageResult;
import com.central.common.model.RegexEnum;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.log.annotation.AuditLog;
import com.central.platform.backend.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
    public PageResult<SysUser> list(@RequestParam Map<String, Object> params) {
        params.put("type", CommonConstant.USER_TYPE_APP);//APP用户数据
        PageResult<SysUser> sysUserList = sysUserService.findSysUserList(params);
        return sysUserList;
    }



    /**
     * 新增or更新 APP用户
     *
     * @param sysUser
     * @return
     */
    @PostMapping("/users/saveOrUpdate")
    @AuditLog(operation = "'新增或更新用户:' + #sysUser.username")
    public Result saveOrUpdate(@RequestBody SysUser sysUser) throws Exception {
        if(StringUtils.isBlank(sysUser.getUsername()) || !sysUser.getUsername().matches(RegexEnum.ACCOUNT.getRegex())){
            return Result.failed(RegexEnum.ACCOUNT.getName() + RegexEnum.ACCOUNT.getDesc());
        }
        if(StringUtils.isBlank(sysUser.getPassword()) || !sysUser.getPassword().matches(RegexEnum.PASSWORDAPP.getRegex())){
            return Result.failed(RegexEnum.PASSWORDAPP.getName() + RegexEnum.PASSWORDAPP.getDesc());
        }
        sysUser.setType(CommonConstant.USER_TYPE_APP);
        return sysUserService.saveOrUpdate(sysUser);
    }

    /**
     * 删除用户
     *
     * @param id
     */
    @DeleteMapping(value = "/users/{id}")
    @AuditLog(operation = "'删除用户:' + #id")
    public Result delete(@PathVariable Long id) {
        return sysUserService.delete(id);
    }

    /**
     * 用户自己修改密码
     */
    @PutMapping(value = "/users/password")
    public Result resetPassword(@RequestBody SysUser sysUser) {
        if(sysUser.getId() == null){
            return Result.failed("会员不存在");
        }
        if(StringUtils.isBlank(sysUser.getPassword()) || !sysUser.getPassword().matches(RegexEnum.PASSWORDAPP.getRegex())){
            return Result.failed(RegexEnum.PASSWORDAPP.getName() + RegexEnum.PASSWORDAPP.getDesc());
        }
        return sysUserService.resetPassword(sysUser);
    }

}
