package com.central.user.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.central.common.annotation.LoginUser;
import com.central.common.constant.CommonConstant;
import com.central.common.constant.SecurityConstants;
import com.central.common.model.*;
import com.central.push.constant.SocketTypeConstant;
import com.central.user.model.co.*;
import com.central.common.utils.ExcelUtil;
import com.central.common.utils.GoogleAuthUtil;
import com.central.common.utils.PwdEncoderUtil;
import com.central.config.feign.ConfigService;
import com.central.log.annotation.AuditLog;
import com.central.push.feign.PushService;
import com.central.search.client.service.IQueryService;
import com.central.search.model.LogicDelDto;
import com.central.search.model.SearchDto;
import com.central.user.model.SysUserExcel;
import com.central.user.model.vo.SysUserInfoMoneyVo;
import com.central.user.service.ISysUserService;
import com.central.user.model.vo.UserInfoVo;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.*;

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

    @Resource
    private ConfigService configService;

    @Autowired
    private IQueryService queryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private PushService pushService;

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

    @ApiOperation(value = "绑定谷歌验证码")
    @PostMapping("/users-anon/bindGoogleCode")
    public Result<String> bindGoogleCode(@Valid @RequestBody SysUserGoogleBindCoCo params) {
        String username = params.getUsername();
        String password = params.getPassword();
        String googleCode = params.getGoogleCode();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(googleCode)){
            return Result.failed("参数必填");
        }
        LoginAppUser loginAppUser = findByUsername(username);
        if (loginAppUser == null || !loginAppUser.getType().equals("BACKEND")) {
            return Result.failed("用户名或密码错误");
        }
        if (loginAppUser.getGaBind() != null && loginAppUser.getGaBind() == 1) {
            return Result.failed("该账号已经绑定谷歌验证码");
        }
        if (StringUtils.isBlank(loginAppUser.getGaKey())) {
            return Result.failed("请先绑定谷歌身份验证器");
        }
        GaBindCo param = new GaBindCo();
        param.setId(loginAppUser.getId());
        param.setGaBind(1);
        Result result = updateGaBind(param);
        if (result != null && result.getResp_code() == 0){
            return Result.succeed();
        }
        return Result.failed("绑定失败");
    }

    @ApiOperation(value = "得到谷歌二维码链接")
    @PostMapping("/users-anon/getGoogleCodeLink")
    public Result<String> getGoogleCodeLink(@Valid @RequestBody SysUserParamsCo params) {
        String username = params.getUsername();
        String password = params.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            return Result.failed("参数必填");
        }
        LoginAppUser loginAppUser = findByUsername(username);
        if (loginAppUser == null || !loginAppUser.getType().equals("BACKEND")) {
            return Result.failed("用户名或密码错误");
        }
        if (!loginAppUser.isEnabled()){
            return Result.failed("该账号状态异常");
        }
        if (loginAppUser.getGaBind() != null && loginAppUser.getGaBind() == 1) {
            return Result.failed("该账号已经绑定谷歌验证码");
        }
        PasswordEncoder encoder = PwdEncoderUtil.getDelegatingPasswordEncoder("bcrypt");
        Boolean match = encoder.matches(password,loginAppUser.getPassword());
        if (!match){
            return Result.failed("用户名或密码错误");
        }
//        if (!passwordEncoder.matches(password, loginAppUser.getPassword())) {
//            return Result.failed("用户名或密码错误");
//        }
        String secret = GoogleAuthUtil.generateSecretKey();
        Map<String, Object> param = new HashMap<>();
        param.put("id",loginAppUser.getId());
        param.put("gaKey",secret);
        Result result = updateGaKey(param);
        if (result != null && result.getResp_code() == 0){
            String qrcode = GoogleAuthUtil.getQcode(username, secret);
            return Result.succeed(qrcode,"");
        }
        return Result.failed("获取谷歌二维码失败");
    }

    /**
     * 查询一个游客用户登录对象LoginAppUser
     */
    @GetMapping(value = "/users-anon/findGuest")
    public Result<LoginAppUser> findGuest() {
        log.info("+++++++++  find guest");
        if (!redisTemplate.hasKey(CommonConstant.PLAYER_ACCOUNT_QUEUE)) {
            return Result.failed("游客已满");
        }
        Object playName = redisTemplate.opsForList().rightPop(CommonConstant.PLAYER_ACCOUNT_QUEUE);
        if (playName == null) {
            return Result.failed("游客已满");
        }
        LoginAppUser loginAppUser = appUserService.findByUsername(playName.toString());
        return Result.succeed(loginAppUser);
    }

    @GetMapping(value = "/user/getOnlineNum")
    @ApiOperation(value = "初始化查询在线人数")
    public Result<Integer> getOnlineNum() {
        String onlineKey = SecurityConstants.REDIS_UNAME_TO_ACCESS + SecurityConstants.APP_USER_ONLINE + "*";
        Set<String> keySet = redisTemplate.keys(onlineKey);
        int playerSize = keySet.size();
        //查询最低在线人数
        Result<String> result = configService.findMinOnlineUserQuantity();
        if (result.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            log.error("查询最低在线人数失败,result={}", result.toString());
        }
        String datas = result.getDatas();
        if (!ObjectUtils.isEmpty(datas)) {
            playerSize = playerSize + Integer.parseInt(datas);
        }
        return Result.succeed(playerSize);
    }

    @GetMapping(value = "/user/pushOnlineNum")
    @ApiOperation(value = "socket推送在线人数",hidden = true)
    public Result pushOnlineNum(@RequestParam("changeNum") Integer changeNum) {
        String onlineKey = SecurityConstants.REDIS_UNAME_TO_ACCESS + SecurityConstants.APP_USER_ONLINE + "*";
        Set<String> keySet = redisTemplate.keys(onlineKey);
        int playerSize = keySet.size();
        if (changeNum != null) {
            playerSize = playerSize + changeNum;
        }
        //查询最低在线人数
        Result<String> result = configService.findMinOnlineUserQuantity();
        if (result.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            log.error("查询最低在线人数失败,result={}", result.toString());
        }
        String datas = result.getDatas();
        if (!ObjectUtils.isEmpty(datas)) {
            playerSize = playerSize + Integer.parseInt(datas);
        }
        PushResult<Integer> pushResult = PushResult.succeed(playerSize, SocketTypeConstant.ONLINE_NUMS, "在线人数推送成功");
        Result<String> push = pushService.sendAllMessage(JSONObject.toJSONString(pushResult));
        log.info("在线人数消息推送结果:{}", push);
        return Result.succeed();
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
    public PageResult<SysUser> findUsers(@Valid @ModelAttribute SysUserListCo params) {
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
    public Result updateEnabled(@ModelAttribute EnabledUserCo params) {
        Long id = params.getId();
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
    public Result updateGaBind(@ModelAttribute GaBindCo params) {
        Long id = params.getId();
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
    public Result updatePassword(@RequestBody SysUser sysUser) {
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
        if(StringUtils.isBlank(sysUser.getUsername()) || !sysUser.getUsername().matches(RegexEnum.ACCOUNT.getRegex())){
            return Result.failed(RegexEnum.ACCOUNT.getName() + RegexEnum.ACCOUNT.getDesc());
        }
        //设置默认头像
        if (sysUser.getId()==null){
            sysUser.setHeadImgUrl(configService.avatarPictureInfo());
        }
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
    public void exportUser(@ModelAttribute SysUserListCo params, HttpServletResponse response) throws IOException {
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


    @ResponseBody
    @ApiOperation(value = "查询注册会员数量")
    @GetMapping("/users/findUserNum")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false),
        @ApiImplicitParam(name = "type", value = "账号类型：APP：前端app用户，APP_GUEST：前端app游客用户，BACKEND：后端管理用户", required = false),
        @ApiImplicitParam(name = "parent", value = "所属平台", required = false),
    })
    public Result<Integer> findUserNum(@RequestParam Map<String, Object> params) {
        Integer userNum = appUserService.findUserNum(params);
        return Result.succeed(userNum);
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

    @ApiOperation(value = "根据ids,查询用户")
    @PostMapping("/users/findListByIds")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ids", value = "用户id", required = true)
    })
    public Result<List<SysUser>> findListByIds(@RequestBody List<Long> ids) {
        List<SysUser> listByIds = appUserService.findListByIds(ids);
        return Result.succeed(listByIds);
    }

    @ApiOperation(value = "根据ids,查询用户基本信息及余额",hidden = true)
    @PostMapping("/users/findListByUserIdList")
    @ApiImplicitParam(name = "userIdList", value = "用户userIdList", required = true)
    public Result<List<SysUserInfoMoneyVo>> findListByUserIdList(@RequestBody List<Long> userIdList) {
        List<SysUserInfoMoneyVo> list = appUserService.findListByUserIds(userIdList);
        return Result.succeed(list);
    }
}
