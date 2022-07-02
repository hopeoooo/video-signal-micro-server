package com.central.user.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.dto.LoginLogPageDto;
import com.central.common.model.*;
import com.central.user.model.co.*;
import com.central.common.vo.SysMoneyVO;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.user.feign.callback.UserServiceFallbackFactory;
import com.central.user.model.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author zlt
 */
@FeignClient(name = ServiceNameConstants.USER_SERVICE, fallbackFactory = UserServiceFallbackFactory.class, decode404 = true)
public interface UserService {
    /**
     * feign rpc访问远程/users/{username}接口
     * 查询用户实体对象SysUser
     *
     * @param username
     * @return
     */
    @GetMapping(value = "/users/name/{username}")
    SysUser selectByUsername(@PathVariable("username") String username);


    @GetMapping("/users/current")
    Result<LoginAppUser> getLoginAppUser() ;
    /**
     * feign rpc访问远程/users-anon/login接口
     *
     * @param username
     * @return
     */
    @GetMapping(value = "/users-anon/login", params = "username")
    LoginAppUser findByUsername(@RequestParam("username") String username);

    @PostMapping("/users-anon/bindGoogleCode")
    Result<String> bindGoogleCode(@RequestBody SysUserGoogleBindCoCo params);

    @PostMapping("/users-anon/getGoogleCodeLink")
    Result<String> getGoogleCodeLink(@RequestBody SysUserParamsCo params);

    /**
     * feign rpc访问远程/users-anon/login1接口
     *
     * @return
     */
    @GetMapping(value = "/users-anon/findGuest")
    Result<LoginAppUser> findGuest();

    /**
     * feign rpc访问远程/users/loginSuc接口
     * 处理登录成功
     * @return
     */
    @PostMapping(value = "/user/loginSuc",params = "loginAppUser")
    Boolean processLoginSuccess(@RequestBody LoginAppUser loginAppUser);

    /**
     * 推送APP在线人数
     * @return
     */
    @GetMapping(value = "/user/pushOnlineNum")
    Result pushOnlineNum(@RequestParam("changeNum") Integer changeNum);

    /**
     * 通过手机号查询用户、角色信息
     *
     * @param mobile 手机号
     */
    @GetMapping(value = "/users-anon/mobile", params = "mobile")
    LoginAppUser findByMobile(@RequestParam("mobile") String mobile);

    /**
     * 根据OpenId查询用户信息
     *
     * @param openId openId
     */
    @GetMapping(value = "/users-anon/openId", params = "openId")
    LoginAppUser findByOpenId(@RequestParam("openId") String openId);

    /**
     * 用户查询列表
     *
     * @param params
     * @return
     */
    @GetMapping(value = "/users", params = "params")
    PageResult<SysUser> findSysUserList(@SpringQueryMap SysUserListCo params);

    /**
     * 查询会员日志列表
     * @return
     */
    @GetMapping(value = "/loginLog/findUserLoginLogList", params = "params")
    PageResult<LoginLogPageDto> findUserLoginLogList(@SpringQueryMap UserLoginLogPageCo params) ;



    /**
     * 查询游客列表
     */
    @GetMapping(value = "/users/players")
    List<SysUser> queryPlayerList();

    /**
     * 更新或添加用户信息
     * @return
     */
    @PostMapping(value = "/users/saveOrUpdate", params = "sysUser")
    Result saveOrUpdate(@RequestBody SysUserCo sysUser);

    /**
     * 根据ID物理删除用户
     *
     * @param openId openId
     */
    @DeleteMapping(value = "/users/{id}", params = "id")
    Result delete(@PathVariable("id") Long openId);

    @PutMapping(value = "/users/{id}/password", params = "SysUser")
    Result resetPassword(@PathVariable("id") Long id) ;

    @PutMapping(value = "/users/password", params = "SysUser")
    Result updatePassword(@RequestBody SysUser sysUser) ;

    @PutMapping(value = "/users/{id}/updateVerify", params = "id")
    Result updateVerify(@PathVariable("id") Long id) ;

    @GetMapping(value ="/users/updateEnabled", params = "SysUser")
    Result updateEnabled(@SpringQueryMap EnabledUserCo params);

    @GetMapping(value ="/users/updateGaKey", params = "SysUser")
    Result updateGaKey(@RequestParam Map<String, Object> params);

    @GetMapping(value ="/users/updateGaBind", params = "SysUser")
    Result updateGaBind(@SpringQueryMap GaBindCo params);

    /**
     * 新增用户钱包
     * @param sysUserMoney
     * @return
     */
    @PostMapping("/userMoney/save")
    Result<SysUserMoney> save(@RequestBody SysUserMoneyCo sysUserMoney);

    @PostMapping("/userMoney/playerMoney")
    Result<Boolean> updateMoney(@RequestBody SysMoneyVO sysMoneyVO);

    @PostMapping(value = "/loginLog/addLog", params = "LoginLog")
    Result<Boolean> addLoginlog(@RequestBody LoginLog loginLog);

    @PostMapping(value = "/userMoney/transterMoney")
    Result<SysUserMoney> transterMoney(@RequestParam("userId") Long userId, @RequestParam("money") BigDecimal money,
                                       @RequestParam("remark") String remark, @RequestParam("transterType") Integer transterType,
                                       @RequestParam("traceId") String traceId,@RequestParam("betId") String betId,
                                       @RequestParam("auditMultiple") BigDecimal auditMultiple);

    @GetMapping(value = "/sysTansterMoney/findList", params = "params")
    PageResult<SysTansterMoneyLogVo> findTransterMoneyList(@SpringQueryMap SysTansterMoneyPageCo params);

    @GetMapping(value = "/sysTansterMoney/findAllByParent")
    Result<List<SysTansterMoneyLogVo>> findAllByParent(@SpringQueryMap SysTansterMoneyPageCo params);

    @GetMapping("/userWashCode/findUserWashCodeConfigList/{userId}")
     Result<List<UserWashCodeConfig>> findUserWashCodeConfigList(@PathVariable("userId") Long userId) ;

    @GetMapping("/userWashCode/findWashCodeConfigListByUserId/{userId}")
    Result<List<UserWashCodeConfig>> findWashCodeConfigListByUserId(@PathVariable("userId") Long userId) ;

    @GetMapping("/userWashCode/findWashCodeConfigListByGameIdAndUserId")
    Result<List<UserWashCodeConfig>> findWashCodeConfigListByGameIdAndUserId(@RequestParam("gameId") Long gameId, @RequestParam("userId") Long userId);

    @PostMapping("/userWashCode/saveUserWashCodeConfig")
    Result saveUserWashCodeConfig(@RequestBody List<UserWashCodeConfigCo> list) ;

    @GetMapping("/users/updateHeadImg")
    Result updateHeadImgUrl(@NotBlank(message = "headImg不允许为空") String headImg);

    @GetMapping("/users/info")
    Result<UserInfoVo> findUserInfoById();

    @GetMapping("/userMoney/getMoney")
    Result<SysUserMoneyVo> getMoney();

    @GetMapping("/userMoney/getMoneyByUserName")
    Result<SysUserMoneyVo> getMoneyByUserName(@RequestParam("userName") String userName);

    @GetMapping("/userMoney/getSumMoneyByParent")
    Result<BigDecimal> getSumMoneyByParent(@RequestParam(value = "parent") String parent);

    @GetMapping("/userMoney/receiveWashCode")
    Result<String> receiveWashCode();

    @GetMapping("/followList/getRoomFollowList")
    Result<List<RoomFollowVo>> getRoomFollowList();

    @PostMapping("/followList/addOrRemoveFollow/{roomId}")
    Result addFollow(@PathVariable("roomId") Long roomId);

    @GetMapping("/users/findUserNum")
    Result findUserNum(@RequestParam Map<String, Object> params);

    @PutMapping("/users")
    void updateSysUser(@RequestBody SysUser sysUser);

    @GetMapping("/sysTansterMoney/findOrderTypeAccountChangeList")
    Result<List<SysTansterMoneyLogVo>> findOrderTypeAccountChangeList(@RequestParam(value ="orderType", required = false) String orderType,@RequestParam(value ="listId", required = false) List<Long> listId) ;

    @PostMapping("/users/findListByIds")
    Result<List<SysUser>> findListByIds(@RequestBody List<Long> ids);

    @PostMapping("/users/findListByUserIdList")
    Result<List<SysUserInfoMoneyVo>> findListByUserIdList(@RequestBody List<Long> userIdList);

    @GetMapping("/loginLog/getLastLoginLogByUserId/{userId}")
    Result<LoginLog> getLastLoginLogByUserId(@PathVariable("userId") Long userId);

    @PostMapping("/userMoney/updateWashCode")
    Result updateWashCode(@RequestParam("userId") Long userId, @RequestParam("washCode") BigDecimal washCode);

    @PostMapping("/userMoney/updateUnfinishedCode")
    Result updateUnfinishedCode(@RequestParam("userId") Long userId, @RequestParam("unfinishedCode") BigDecimal unfinishedCode);

    @PostMapping(value = "/userAudit/findUserAuditList")
    PageResult<SysUserAuditVo> findUserAuditList(@RequestBody SysUserAuditPageCo params);

    @PostMapping(value = "/userAudit/addAudit")
    void addAudit(@RequestBody AddUserAuditCo addUserAuditCo);

    @PostMapping(value = "/userAudit/subtractAudit")
    void subtractAudit(@RequestBody AddUserAuditCo addUserAuditCo);

}
