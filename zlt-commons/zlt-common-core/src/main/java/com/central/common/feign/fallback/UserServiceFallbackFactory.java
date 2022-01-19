package com.central.common.feign.fallback;

import com.central.common.dto.LoginLogPageDto;
import com.central.common.feign.UserService;
import com.central.common.model.*;
import com.central.common.vo.SysMoneyVO;
import com.central.common.vo.SysTansterMoneyLogVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            public LoginAppUser findGuest(){
                log.error("通过游客用户异常:{}", throwable.getMessage());
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
            public  Result updateVerify(Long id) {
                log.error("updateVerify修改Verify:{}", id, throwable);
                return Result.failed("修改Verify失败");
            }

            @Override
            public Result updateEnabled(Map<String, Object> params) {
                log.error("updateEnabled修改状态异常:{}", params, throwable);
                return Result.failed("修改状态失败");
            }

            @Override
            public Result updateGaKey(Map<String, Object> params) {
                log.error("updateGaKey修改二维码key异常:{}", params, throwable);
                return Result.failed("修改二维码key失败");
            }

            @Override
            public Result updateGaBind(Map<String, Object> params) {
                log.error("updateGaBind修改绑定二维码状态异常:{}", params, throwable);
                return Result.failed("修改绑定二维码状态失败");
            }

            @Override
            public Result<SysUserMoney> save(SysUserMoney sysUserMoney) {
                log.error("新增用户钱包失败:{}", sysUserMoney, throwable);
                return Result.failed("新增用户钱包失败");
            }

            @Override
            public Result<Boolean> updateMoney(SysMoneyVO sysMoneyVO) {
                log.error("初始化游客金额失败",sysMoneyVO);
                return Result.failed("初始化游客金额失败");
            }

            @Override
            public Result<Boolean> addLoginlog(LoginLog loginLog) {
                log.error("新增登录日志失败:{}",loginLog,throwable);
                return Result.failed("新增登录日志失败");
            }

            @Override
            public Result<SysUserMoney> transterMoney(Long userId, BigDecimal money, String remark, Boolean transterType) {
                log.error("人工上下分:{}",userId,throwable);
                return Result.failed("上线分错误");
            }

            @Override
            public PageResult2<SysTansterMoneyLogVo> findTransterMoneyList(Map<String, Object> params) {
                log.error("findTransterMoneyList查询会员账变异常:{}", params, throwable);
                return new PageResult2();
            }
        };
    }
}
