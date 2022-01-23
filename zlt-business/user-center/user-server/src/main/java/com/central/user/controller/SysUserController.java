package com.central.user.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.central.common.annotation.LoginUser;
import com.central.common.constant.CommonConstant;
import com.central.common.model.*;
import com.central.common.utils.ExcelUtil;
import com.central.log.annotation.AuditLog;
import com.central.search.client.service.IQueryService;
import com.central.search.model.LogicDelDto;
import com.central.search.model.SearchDto;
import com.central.user.model.SysUserExcel;
import com.central.user.service.ISysUserService;
import com.central.user.vo.UserInfoVo;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 作者 owen E-mail: 624191343@qq.com
 * 用户
 */
@Slf4j
@RestController
@Api(tags = "用户模块api")
@Validated
public class SysUserController {
    private static final String ADMIN_CHANGE_MSG = "超级管理员不给予修改";

    /**
     * 全文搜索逻辑删除Dto
     */
    private static final LogicDelDto SEARCH_LOGIC_DEL_DTO = new LogicDelDto("isDel", "否");

    @Autowired
    private ISysUserService appUserService;

    @Autowired
    private IQueryService queryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 当前登录用户 LoginAppUser
     *
     * @return
     */
    @ApiOperation(value = "根据access_token当前登录用户")
    @GetMapping("/users/current")
    public Result<LoginAppUser> getLoginAppUser(@LoginUser SysUser user) {
        SysUser sysUser = appUserService.selectById(user.getId());
        return Result.succeed(appUserService.getLoginAppUser(sysUser));
    }

    /**
     * 查询用户实体对象SysUser
     */
    @GetMapping(value = "/users/name/{username}")
    @ApiOperation(value = "根据用户名查询用户实体")
    public SysUser selectByUsername(@PathVariable String username) {
        return appUserService.selectByUsername(username);
    }

    /**
     * 查询用户登录对象LoginAppUser
     */
    @GetMapping(value = "/users-anon/login", params = "username")
    @ApiOperation(value = "根据用户名查询用户")
    public LoginAppUser findByUsername(String username) {
        return appUserService.findByUsername(username);
    }

    /**
     * 查询一个游客用户登录对象LoginAppUser
     */
    @GetMapping(value = "/users-anon/findGuest")
    public LoginAppUser findGuest(){
        log.info("+++++++++  find guest");

        if(!redisTemplate.hasKey(CommonConstant.PLAYER_ACCOUNT_QUEUE))
            throw new InternalAuthenticationServiceException("游客已使用完");

        Object playName = redisTemplate.opsForList().rightPop(CommonConstant.PLAYER_ACCOUNT_QUEUE);
        if(playName ==null)
            throw new InternalAuthenticationServiceException("游客已满");

        return appUserService.findByUsername(playName.toString());
    }

    /**
     * 通过手机号查询用户、角色信息
     *
     * @param mobile 手机号
     */
    @GetMapping(value = "/users-anon/mobile", params = "mobile")
    @ApiOperation(value = "根据手机号查询用户")
    public SysUser findByMobile(String mobile) {
        return appUserService.findByMobile(mobile);
    }

    /**
     * 根据OpenId查询用户信息
     *
     * @param openId openId
     */
    @GetMapping(value = "/users-anon/openId", params = "openId")
    @ApiOperation(value = "根据OpenId查询用户")
    public SysUser findByOpenId(String openId) {
        return appUserService.findByOpenId(openId);
    }

    @GetMapping("/users/{id}")
    public SysUser findUserById(@PathVariable Long id) {
        return appUserService.selectById(id);
    }

    @GetMapping("/users/info")
    @ApiOperation(value = "查询登录用户基本信息")
    public Result<UserInfoVo> findUserInfoById(@LoginUser SysUser user) {
        SysUser sysUser = appUserService.selectById(user.getId());
        UserInfoVo vo = new UserInfoVo();
        BeanUtil.copyProperties(sysUser, vo);
        vo.setIsAutoBet(vo.getIsAutoBet() == null ? false : vo.getIsAutoBet());
        return Result.succeed(vo);
    }

    /**
     * 管理后台修改用户
     *
     * @param sysUser
     */
    @PutMapping("/users")
    //@AuditLog(operation = "'更新用户:' + #sysUser")
    public void updateSysUser(@RequestBody SysUser sysUser) {
        cacheEvictUser(sysUser.getId());
        appUserService.updateById(sysUser);
    }

    /**
     * 管理后台给用户分配角色
     *
     * @param id
     * @param roleIds
     */
    @PostMapping("/users/{id}/roles")
    public void setRoleToUser(@PathVariable Long id, @RequestBody Set<Long> roleIds) {
        appUserService.setRoleToUser(id, roleIds);
    }

    /**
     * 获取用户的角色
     *
     * @param
     * @return
     */
    @GetMapping("/users/{id}/roles")
    public List<SysRole> findRolesByUserId(@PathVariable Long id) {
        return appUserService.findRolesByUserId(id);
    }

    /**
     * 用户查询
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "用户查询列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer")
    })
    @GetMapping("/users")
    public PageResult<SysUser> findUsers(@RequestParam Map<String, Object> params) {
        return appUserService.findUsers(params);
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
            @ApiImplicitParam(name = "enabled", value = "是否启用", required = true, dataType = "Boolean")
    })
    public Result updateEnabled(@RequestParam Map<String, Object> params) {
        Long id = MapUtils.getLong(params, "id");
        if (checkAdmin(id)) {
            return Result.failed(ADMIN_CHANGE_MSG);
        }
        cacheEvictUser(id);
        return appUserService.updateEnabled(params);
    }

    /**
     * 二维码code变更
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "二维码code变更")
    @GetMapping("/users/updateGaKey")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "gaKey", value = "谷歌验证码KEY", required = true, dataType = "String")
    })
    public Result updateGaKey(@RequestParam Map<String, Object> params) {
        Long id = MapUtils.getLong(params, "id");
        cacheEvictUser(id);
        return appUserService.updateGaKey(params);
    }

    /**
     * 二维码绑定状态变更
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "二维码绑定状态变更")
    @GetMapping("/users/updateGaBind")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "gaBind", value = "谷歌验证码是否绑定1 1：已绑定，其他：未绑定", required = true, dataType = "Integer")
    })
    public Result updateGaBind(@RequestParam Map<String, Object> params) {
        Long id = MapUtils.getLong(params, "id");
        cacheEvictUser(id);
        return appUserService.updateGaBind(params);
    }

    /**
     * 谷歌验证码是否校验状态修改
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "谷歌验证码是否校验状态修改")
    @PutMapping("/users/{id}/updateVerify")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Integer"),
    })
    public Result updateVerify(@PathVariable Long id) {
        if (checkAdmin(id)) {
            return Result.failed(ADMIN_CHANGE_MSG);
        }
        cacheEvictUser(id);
        return appUserService.updateVerify(id);
    }
    /**
     * 管理后台，给用户重置密码
     *
     * @param id
     */
    @PutMapping(value = "/users/{id}/password")
    //@AuditLog(operation = "'重置用户密码:' + #id")
    public Result resetPassword(@PathVariable Long id) {
        if (checkAdmin(id)) {
            return Result.failed(ADMIN_CHANGE_MSG);
        }
        cacheEvictUser(id);
        String password = appUserService.resetUpdatePassword(id);
        return Result.succeed(password,"重置成功");
    }

    /**
     * 用户自己修改密码
     */
    @PutMapping(value = "/users/password")
    public Result resetPassword(@RequestBody SysUser sysUser) {
        if (checkAdmin(sysUser.getId())) {
            return Result.failed(ADMIN_CHANGE_MSG);
        }
        cacheEvictUser(sysUser.getId());
        appUserService.updatePassword(sysUser.getId(), sysUser.getOldPassword(), sysUser.getNewPassword());
        return Result.succeed("重置成功");
    }

    /**
     * 删除用户
     *
     * @param id
     */
    @DeleteMapping(value = "/users/{id}")
    //@AuditLog(operation = "'删除用户:' + #id")
    public Result delete(@PathVariable Long id) {
        if (checkAdmin(id)) {
            return Result.failed(ADMIN_CHANGE_MSG);
        }
        cacheEvictUser(id);
        appUserService.delUser(id);
        return Result.succeed("删除成功");
    }


    /**
     * 新增or更新
     *
     * @param sysUser
     * @return
     */
    @CacheEvict(value = "user", key = "#sysUser.username")
    @PostMapping("/users/saveOrUpdate")
    @AuditLog(operation = "'新增或更新用户:' + #sysUser.username")
    public Result saveOrUpdate(@RequestBody SysUser sysUser) throws Exception {
        if (sysUser.getId() != null) {
            cacheEvictUser(sysUser.getId());
        }
        Result result = appUserService.saveOrUpdateUser(sysUser);
        return result;
    }

    /**
     * 导出excel
     *
     * @return
     */
    @PostMapping("/users/export")
    public void exportUser(@RequestParam Map<String, Object> params, HttpServletResponse response) throws IOException {
        List<SysUserExcel> result = appUserService.findAllUsers(params);
        //导出操作
        ExcelUtil.exportExcel(result, null, "用户", SysUserExcel.class, "user", response);
    }

    @PostMapping(value = "/users/import")
    public Result importExcl(@RequestParam("file") MultipartFile excl) throws Exception {
        int rowNum = 0;
        if(!excl.isEmpty()) {
            List<SysUserExcel> list = ExcelUtil.importExcel(excl, 0, 1, SysUserExcel.class);
            rowNum = list.size();
            if (rowNum > 0) {
                List<SysUser> users = new ArrayList<>(rowNum);
                list.forEach(u -> {
                    SysUser user = new SysUser();
                    BeanUtil.copyProperties(u, user);
                    user.setPassword(CommonConstant.DEF_USER_PASSWORD);
                    user.setType(UserType.BACKEND.name());
                    users.add(user);
                });
                appUserService.saveBatch(users);
            }
        }
        return Result.succeed("导入数据成功，一共【"+rowNum+"】行");
    }

    @ApiOperation(value = "用户全文搜索列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "queryStr", value = "搜索关键字", dataType = "String")
    })
    @GetMapping("/users/search")
    public PageResult<JsonNode> search(SearchDto searchDto) {
        searchDto.setIsHighlighter(true);
        searchDto.setSortCol("createTime");
        return queryService.strQuery("sys_user", searchDto, SEARCH_LOGIC_DEL_DTO);
    }

    @GetMapping("/users/players")
    public List<SysUser> queryPlayerList(){
        List<SysUser> playList = appUserService.lambdaQuery().eq(SysUser::getType,"APP_GUEST").list();
        return playList;
    }

    @ApiOperation(value = "登录用户修改头像")
    @GetMapping("/users/updateHeadImg")
    @ApiImplicitParam(name = "headImg", value = "头像地址,只需要传这一个参数，其他参数为框架多余展示的不用理会", required = true, dataType = "String")
    public Result updateHeadImgUrl(@LoginUser SysUser user,@NotBlank(message = "headImg不允许为空") String headImg) {
        Long id = user.getId();
        cacheEvictUser(id);
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getId,id);
        updateWrapper.set(SysUser::getHeadImgUrl,headImg);
        appUserService.update(updateWrapper);
        return Result.succeed();
    }

    @ApiOperation(value = "登录用户开启/关闭投注自动提交")
    @GetMapping("/users/updateIsAutoBet")
    @ApiImplicitParam(name = "isAutoBet", value = "投注自动提交 false：否，true：是,只需要传这一个参数，其他参数为框架多余展示的不用理会", required = true, dataType = "boolean")
    public Result updateIsAutoBet(@LoginUser SysUser user, boolean isAutoBet) {
        Long id = user.getId();
        cacheEvictUser(id);
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getId,id);
        updateWrapper.set(SysUser::getIsAutoBet,isAutoBet);
        appUserService.update(updateWrapper);
        return isAutoBet == true ? Result.succeed("投注自动提交已开启") : Result.succeed("投注自动提交已关闭");
    }

    @ApiOperation(value = "登录用户修改登录密码")
    @GetMapping("/users/updateLoginPassword")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldLoginPassword", value = "原登录密码", required = true),
            @ApiImplicitParam(name = "newLoginPassword", value = "新登录密码", required = true),
            @ApiImplicitParam(name = "confirmLoginPassword", value = "确认登录密码", required = true)})
    public Result updateIsAutoBet(@LoginUser SysUser user, @NotBlank(message = "oldLoginPassword不允许为空") String oldLoginPassword, @NotBlank(message = "newLoginPassword不允许为空") String newLoginPassword, @NotBlank(message = "confirmLoginPassword不允许为空") String confirmLoginPassword) {
        if (oldLoginPassword.equals(newLoginPassword)) {
            return Result.failed("两次输入密码不匹配，请仔细确认");
        }
        if (!newLoginPassword.equals(confirmLoginPassword)) {
            return Result.failed("两次输入密码不匹配，请仔细确认");
        }
        if (!newLoginPassword.matches(RegexEnum.ACCOUNT.getRegex())) {
            return Result.failed("密码" + RegexEnum.ACCOUNT.getDesc());
        }
        Long id = user.getId();
        SysUser sysUser = appUserService.selectById(id);
        if (!passwordEncoder.matches(oldLoginPassword, sysUser.getPassword())) {
            return Result.failed("原登录密码填写错误");
        }
        cacheEvictUser(id);
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getId, id);
        updateWrapper.set(SysUser::getPassword, passwordEncoder.encode(newLoginPassword));
        appUserService.update(updateWrapper);
        return Result.succeed();
    }



    /**
     * 清除缓存
     */
    public void cacheEvictUser(Long id){
        SysUser sysUser = appUserService.selectById(id);
        if (sysUser != null) {
            appUserService.cacheEvictUser(sysUser);
        }
    }

    /**
     * 是否超级管理员
     */
    private boolean checkAdmin(long id) {
        return id == 1L;
    }
}
