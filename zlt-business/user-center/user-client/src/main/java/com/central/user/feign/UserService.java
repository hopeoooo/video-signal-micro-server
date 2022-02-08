package com.central.user.feign;

import com.central.common.annotation.LoginUser;
import com.central.common.constant.ServiceNameConstants;
import com.central.common.dto.LoginLogPageDto;
import com.central.common.model.*;
import com.central.common.vo.SysMoneyVO;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.user.feign.callback.UserServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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

    /**
     * feign rpc访问远程/users-anon/login接口
     *
     * @param username
     * @return
     */
    @GetMapping(value = "/users-anon/login", params = "username")
    LoginAppUser findByUsername(@RequestParam("username") String username);

    /**
     * feign rpc访问远程/users-anon/login1接口
     *
     * @return
     */
    @GetMapping(value = "/users-anon/findGuest")
    LoginAppUser findGuest();

    /**
     * feign rpc访问远程/users/loginSuc接口
     * 处理登录成功
     * @return
     */
    @PostMapping(value = "/user/loginSuc",params = "loginAppUser")
    Boolean processLoginSuccess(@RequestBody LoginAppUser loginAppUser);

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
    PageResult<SysUser> findSysUserList(@RequestParam("params") Map<String, Object> params);

    /**
     * 查询会员日志列表
     * @return
     */
    @GetMapping(value = "/loginLog/findUserLoginLogList", params = "params")
    PageResult<LoginLogPageDto> findUserLoginLogList(@RequestParam("params") Map<String, Object> params) ;



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
    Result saveOrUpdate(@RequestBody SysUser sysUser);

    /**
     * 根据ID物理删除用户
     *
     * @param openId openId
     */
    @DeleteMapping(value = "/users/{id}", params = "id")
    Result delete(@PathVariable("id") Long openId);

    @PutMapping(value = "/users/{id}/password", params = "SysUser")
    Result resetPassword(@PathVariable("id") Long id) ;

    @PutMapping(value = "/users/{id}/updateVerify", params = "id")
    Result updateVerify(@PathVariable("id") Long id) ;

    @GetMapping(value ="/users/updateEnabled", params = "SysUser")
    Result updateEnabled(@RequestParam Map<String, Object> params);

    @GetMapping(value ="/users/updateGaKey", params = "SysUser")
    Result updateGaKey(@RequestParam Map<String, Object> params);

    @GetMapping(value ="/users/updateGaBind", params = "SysUser")
    Result updateGaBind(@RequestParam Map<String, Object> params);

    /**
     * 新增用户钱包
     * @param sysUserMoney
     * @return
     */
    @PostMapping("/userMoney/save")
    Result<SysUserMoney> save(@RequestBody SysUserMoney sysUserMoney);

    @PostMapping("/userMoney/playerMoney")
    Result<Boolean> updateMoney(@RequestBody SysMoneyVO sysMoneyVO);

    @PostMapping(value = "/loginLog/addLog", params = "LoginLog")
    Result<Boolean> addLoginlog(@RequestBody LoginLog loginLog);

    @PostMapping(value = "/userMoney/transterMoney")
    Result<SysUserMoney> transterMoney(@RequestParam("userId") Long userId, @RequestParam("money") BigDecimal money,
                                       @RequestParam("remark") String remark, @RequestParam("transterType") Boolean transterType);

    @GetMapping(value = "/sysTansterMoney/findList", params = "params")
    PageResult<SysTansterMoneyLogVo> findTransterMoneyList(@RequestParam("params") Map<String, Object> params);


    @GetMapping("/userWashCode/findUserWashCodeConfigList/{userId}")
     Result< List<UserWashCodeConfig>> findUserWashCodeConfigList(@PathVariable("userId") Long userId) ;


    @PostMapping("/userWashCode/saveUserWashCodeConfig")
    Result saveUserWashCodeConfig(@RequestBody List<UserWashCodeConfig> list) ;
}
