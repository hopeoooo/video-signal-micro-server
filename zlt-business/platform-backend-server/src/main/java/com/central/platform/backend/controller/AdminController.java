package com.central.platform.backend.controller;

import com.central.common.constant.CommonConstant;
import com.central.common.model.PageResult;
import com.central.common.model.RegexEnum;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.log.annotation.AuditLog;
import com.central.user.feign.UserService;
import com.central.user.model.co.GaBindCo;
import com.central.user.model.co.SysUserCo;
import com.central.user.model.co.SysUserListCo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Api(tags = "管理员中心")
@Slf4j
@RequestMapping("/platform/admin")
public class AdminController {

    @Resource
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 后台管理员查询
     */
    @ApiOperation(value = "后台管理员查询")

    @GetMapping("/users/list")
    public Result<PageResult<SysUser>> list(@ModelAttribute SysUserListCo params) {
        params.setType(CommonConstant.USER_TYPE_BACKEND);//APP用户数据
        PageResult<SysUser> sysUserList = userService.findSysUserList(params);
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
    public Result saveOrUpdate(@RequestBody SysUserCo sysUser){
        if(StringUtils.isBlank(sysUser.getUsername()) || !sysUser.getUsername().matches(RegexEnum.ACCOUNT.getRegex())){
            return Result.failed(RegexEnum.ACCOUNT.getName() + RegexEnum.ACCOUNT.getDesc());
        }
        sysUser.setType(CommonConstant.USER_TYPE_BACKEND);
        return userService.saveOrUpdate(sysUser);
    }

    /**
     * 谷歌验证码是否校验状态修改
     */
    @ApiOperation(value = "谷歌验证码是否校验状态修改")
    @PutMapping(value = "/users/{id}/updateVerify")
    public Result updateVerify(@PathVariable Long id) {
        return userService.updateVerify(id);
    }

    /**
     * 重置密码
     */
    @ApiOperation(value = "重置密码")
    @PutMapping(value = "/users/{id}/password")
    public Result resetPasswords(@PathVariable Long id) {
        return userService.resetPassword(id);
    }

    /**
     * 重置谷歌验证码
     */
    @ApiOperation(value = "重置谷歌验证码")
    @PutMapping(value = "/users/{id}/resetGoogleCode")
    public Result resetGoogleCode(@PathVariable Long id) {
        GaBindCo param = new GaBindCo();
        param.setId(id);
        param.setGaBind(2);

        Result result = userService.updateGaBind(param);
        if (result != null && result.getResp_code() == 0){
            return Result.succeed();
        }
        return Result.failed("重置失败");
    }
}
