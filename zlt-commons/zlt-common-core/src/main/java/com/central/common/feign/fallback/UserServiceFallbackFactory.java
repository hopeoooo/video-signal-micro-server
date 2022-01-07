package com.central.common.feign.fallback;

import com.central.common.dto.LoginLogPageDto;
import com.central.common.feign.UserService;
import com.central.common.model.*;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * userService降级工场
 *
 * @author zlt
 * @date 2019/1/18
 */
@Slf4j
public class UserServiceFallbackFactory implements FallbackFactory<UserService> {
    @Override
    public UserService create(Throwable throwable) {
        return new UserService() {
            @Override
            public SysUser selectByUsername(String username) {
                log.error("通过用户名查询用户异常:{}", username, throwable);
                return new SysUser();
            }

            @Override
            public LoginAppUser findByUsername(String username) {
                log.error("通过用户名查询用户异常:{}", username, throwable);
                return new LoginAppUser();
            }

            @Override
            public LoginAppUser findByMobile(String mobile) {
                log.error("通过手机号查询用户异常:{}", mobile, throwable);
                return new LoginAppUser();
            }

            @Override
            public LoginAppUser findByOpenId(String openId) {
                log.error("通过openId查询用户异常:{}", openId, throwable);
                return new LoginAppUser();
            }

            @Override
            public PageResult2<SysUser> findSysUserList(Map<String, Object> params) {
                log.error("findSysUserList查询用户异常:{}", params, throwable);
                return new PageResult2();
            }
            @Override
            public PageResult2<LoginLogPageDto> findUserLoginLogList(Map<String, Object> params) {
                log.error("findUserLoginLogList查询会员日志异常:{}", params, throwable);
                return new PageResult2();
            }

            @Override
            public List<SysUser> queryPlayerList() {
                return new ArrayList<>();
            }

            @Override
            public Result saveOrUpdate(SysUser sysUser) {
                log.error("saveOrUpdate新增或修改用户数据异常:{}", sysUser, throwable);
                return Result.failed("更新失败");
            }

            @Override
            public Result delete(Long openId) {
                log.error("delete删除用户异常:{}", openId, throwable);
                return Result.failed("删除用户失败");
            }

            @Override
            public  Result resetPassword(Long id) {
                log.error("resetPassword修改密码异常:{}", id, throwable);
                return Result.failed("修改密码失败");
            }

            @Override
            public Result updateEnabled(Map<String, Object> params) {
                log.error("updateEnabled修改状态异常:{}", params, throwable);
                return Result.failed("修改状态失败");
            }

            @Override
            public Result<SysUserMoney> save(SysUserMoney sysUserMoney) {
                log.error("新增用户钱包失败:{}", sysUserMoney, throwable);
                return Result.failed("新增用户钱包失败");
            }

            @Override
            public Result<Boolean> addLoginlog(LoginLog loginLog) {
                log.error("新增登录日志失败:{}",loginLog,throwable);
                return Result.failed("新增登录日志失败");
            }
        };
    }
}
