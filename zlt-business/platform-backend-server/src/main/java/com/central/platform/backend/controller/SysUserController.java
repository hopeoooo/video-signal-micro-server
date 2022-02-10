package com.central.platform.backend.controller;

import com.central.common.constant.CommonConstant;
import com.central.common.constant.SecurityConstants;
import com.central.common.constant.UserConstant;
import com.central.common.model.*;
import com.central.common.redis.template.RedisRepository;
import com.central.log.annotation.AuditLog;
import com.central.user.feign.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@Api(tags = "会员中心")
@Slf4j
@RequestMapping("/platform/backend")
public class SysUserController {

    @Resource
    private UserService userService;
    @Autowired
    private RedisRepository redisRepository;


    /**
     * 用户列表查询。查询APP用户数据
     */
    @ApiOperation(value = "用户列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "username", value = "会员账号", required = false, dataType = "String"),
            @ApiImplicitParam(name = "isOpen", value = "是否模糊查询(0:不勾选 1:勾选)", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "enabled", value = "状态：0.禁用，1.启用", required = false, dataType = "Boolean")
    })
    @GetMapping("/users/list")
    public Result<PageResult<SysUser>> list(@RequestParam Map<String, Object> params) {
        params.put("type", CommonConstant.USER_TYPE_APP);//APP用户数据
        PageResult<SysUser> sysUserList = userService.findSysUserList(params);
        return Result.succeed(sysUserList);
    }

    /**
     * 获取用户是否在线
     */
    @ApiOperation(value = "获取用户是否在线")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "会员账号", required = true, dataType = "String")
    })
    @GetMapping("/users/online")
    public Result findOnlineUser(String username) {
        if(StringUtils.isBlank(username)){
            return Result.failed("参数必传");
        }
        String redisKey = SecurityConstants.REDIS_UNAME_TO_ACCESS+"online:"+username;
        boolean exists = redisRepository.exists(redisKey);
        return Result.succeed(exists);
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

        sysUser.setType(CommonConstant.USER_TYPE_APP);
        return userService.saveOrUpdate(sysUser);
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
        return userService.delete(id);
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
        return userService.updateEnabled(params);
    }


    /**
     * 重置密码
     */
    @ApiOperation(value = "重置密码")
    @PutMapping(value = "/users/{id}/password")
    public Result resetPasswords(@PathVariable Long id) {
        return userService.resetPassword(id);
    }


    @ApiOperation(value = "上下分")
    @PostMapping("/transterMoney")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "money", value = "金额", required = true, dataType = "BigDecimal"),
            @ApiImplicitParam(name = "remark", value = "备注", dataType = "String"),
            @ApiImplicitParam(name = "transterType", value = "0：人工下分, 1：人工上分", required = true, dataType = "Boolean")
    })
    public Result<SysUserMoney> transterMoney(Long userId, BigDecimal money, String remark, Boolean transterType){

        if(money.compareTo(UserConstant.maxTransterMoney) >= 0){
            return Result.failed("用户上分金额太大");
        }
        return userService.transterMoney(userId, money, remark, transterType);
    }
}
