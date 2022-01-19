package com.central.platform.backend.controller;

import com.central.common.constant.CommonConstant;
import com.central.common.model.PageResult2;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api(tags = "管理员中心")
@Slf4j
@RequestMapping("/platform/admin")
public class AdminController {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 后台管理员查询
     */
    @ApiOperation(value = "后台管理员查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "username", value = "会员账号", required = false, dataType = "String"),
            @ApiImplicitParam(name = "enabled", value = "状态：0.禁用，1.启用", required = false, dataType = "Boolean")
    })
    @GetMapping("/users/list")
    public Result<PageResult2<SysUser>> list(@RequestParam Map<String, Object> params) {
        params.put("type", CommonConstant.USER_TYPE_BACKEND);//APP用户数据
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
    public Result saveOrUpdate(@RequestBody SysUser sysUser){
        if(StringUtils.isBlank(sysUser.getUsername()) || !sysUser.getUsername().matches(RegexEnum.ACCOUNT.getRegex())){
            return Result.failed(RegexEnum.ACCOUNT.getName() + RegexEnum.ACCOUNT.getDesc());
        }
        sysUser.setType(CommonConstant.USER_TYPE_BACKEND);
        return sysUserService.saveOrUpdate(sysUser);
    }

    /**
     * 重置密码
     */
    @ApiOperation(value = "重置密码")
    @PutMapping(value = "/users/{id}/password")
    public Result resetPasswords(@PathVariable Long id) {
        return sysUserService.resetPassword(id);
    }


    /**
     * 重置谷歌验证码
     */
    @ApiOperation(value = "重置谷歌验证码")
    @PutMapping(value = "/users/{id}/resetGoogleCode")
    public Result resetGoogleCode(@PathVariable Long id) {
        Map<String, Object> param = new HashMap<>();
        param.put("id",id);
        param.put("gaBind",2);
        Result result = sysUserService.updateGaBind(param);
        if (result != null && result.getResp_code() == 0){
            return Result.succeed();
        }
        return Result.failed("重置失败");
    }
}
