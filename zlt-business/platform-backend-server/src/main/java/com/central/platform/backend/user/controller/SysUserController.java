package com.central.platform.backend.user.controller;

import com.central.common.constant.CommonConstant;
import com.central.common.feign.UserService;
import com.central.common.model.PageResult;
import com.central.common.model.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@Api(tags = "会员中心")
@Slf4j
@RequestMapping("/sysUser")
public class SysUserController {

    @Resource
    private UserService userService;

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
    @GetMapping("/list")
    public PageResult<SysUser> list(@RequestParam Map<String, Object> params) {
        params.put("type", CommonConstant.USER_TYPE_APP);//APP用户数据
        PageResult<SysUser> sysUserList = userService.findSysUserList(params);
        return sysUserList;
    }
}
