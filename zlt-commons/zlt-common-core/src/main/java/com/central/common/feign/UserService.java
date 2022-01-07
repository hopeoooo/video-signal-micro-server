package com.central.common.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.dto.LoginLogPageDto;
import com.central.common.feign.fallback.UserServiceFallbackFactory;
import com.central.common.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import java.util.List;

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
    PageResult2<SysUser> findSysUserList(@RequestParam("params") Map<String, Object> params);

    /**
     * 查询游客管理配置
     * @return
     */
    @GetMapping(value = "/system/findTouristAmount")
    SysPlatformConfig findTouristAmount();

    /**
     * 编辑游客管理配置
     * @return
     */
    @PostMapping(value = "/system/saveTourist", params = "params")
    Result saveTourist(@RequestParam("params") Map<String, String> params);


    /**
     * 查询会员日志列表
     * @return
     */
    @GetMapping(value = "/loginLog/findUserLoginLogList", params = "params")
    PageResult2<LoginLogPageDto> findUserLoginLogList(@RequestParam("params") Map<String, Object> params) ;



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

    @PutMapping(value = "/users/password", params = "SysUser")
    Result resetPassword(@RequestBody SysUser sysUser);


    @GetMapping(value ="/users/updateEnabled", params = "SysUser")
    Result updateEnabled(@RequestParam Map<String, Object> params);


}
