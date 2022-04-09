package com.central.user.feign.callback;

import com.central.common.dto.LoginLogPageDto;
import com.central.common.model.*;
import com.central.user.model.co.*;
import com.central.common.vo.SysMoneyVO;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.user.feign.UserService;
import com.central.user.model.vo.RoomFollowVo;
import com.central.user.model.vo.SysUserMoneyVo;
import com.central.user.model.vo.UserInfoVo;
import com.central.user.model.vo.WashCodeChangeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * userService降级工场
 */
@Slf4j
public class UserServiceFallbackFactory implements FallbackFactory<UserService> {
    @Override
    public UserService create(Throwable throwable) {
        /**
         * TODO 等待验证所有FallbackFactory是否完成
         */
        return new UserService() {
//            @Override
//            public SysUser selectByUsername(String username) {
//                log.error("通过用户名查询用户异常:{}", username, throwable);
//                return new SysUser();
//            }

            @Override
            public SysUser selectByUsername(String username) {
                log.error("服务器异常，selectByUsername通过用户名查询用户异常:{}", username);
                return null;
            }

            @Override
            public Result<LoginAppUser> getLoginAppUser() {
                log.error("服务器异常，getLoginAppUser根据access_token当前登录用户异常" ,throwable);
                return Result.failed("查询当前登录用户失败");
            }

            @GetMapping(value = "/users-anon/login", params = "username")
            @Override
            public LoginAppUser findByUsername(String username) {
//                log.error("通过用户名查询用户异常:{}", username, throwable);
                log.error("服务器异常，findByUsername通过用户名查询用户异常:{}", username);
                return null;
            }

            @Override
            public Result<String> bindGoogleCode(SysUserGoogleBindCoCo params) {
                log.error("服务器异常，bindGoogleCode异常:{}", params);
                return null;
            }

            @Override
            public Result<String> getGoogleCodeLink(SysUserParamsCo params) {
                log.error("服务器异常，getGoogleCodeLink异常:{}", params);
                return null;
            }

            @Override
            public LoginAppUser findGuest(){
                log.error("通过游客用户异常:{}", throwable.getMessage());
                return new LoginAppUser();
            }

            @Override
            public Boolean processLoginSuccess(LoginAppUser loginAppUser) {
                log.error("处理登录成功失败.",throwable);
                return Boolean.FALSE;
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
            public PageResult<SysUser> findSysUserList(SysUserListCo params) {
                log.error("findSysUserList查询用户异常:{}", params, throwable);
                return new PageResult();
            }

            @Override
            public PageResult<LoginLogPageDto> findUserLoginLogList(UserLoginLogPageCo params) {
                log.error("findUserLoginLogList查询会员日志异常:{}", params, throwable);
                return new PageResult();
            }

            @Override
            public List<SysUser> queryPlayerList() {
                return new ArrayList<>();
            }

            @Override
            public Result saveOrUpdate(SysUserCo sysUser) {
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
            public Result updateEnabled(EnabledUserCo params) {
                log.error("updateEnabled修改状态异常:{}", params, throwable);
                return Result.failed("修改状态失败");
            }

            @Override
            public Result updateGaKey(Map<String, Object> params) {
                log.error("updateGaKey修改二维码key异常:{}", params, throwable);
                return Result.failed("修改二维码key失败");
            }

            @Override
            public Result updateGaBind(GaBindCo params) {
                log.error("updateGaBind修改绑定二维码状态异常:{}", params, throwable);
                return Result.failed("修改绑定二维码状态失败");
            }

            @Override
            public Result<SysUserMoney> save(SysUserMoneyCo sysUserMoney) {
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
            public Result<SysUserMoney> transterMoney(Long userId, BigDecimal money, String remark, Integer transterType,String traceId,String betId) {
                log.error("人工上下分:{},transterType={}",userId,transterType,throwable);
                return Result.failed("上下分错误");
            }
            @Override
            public PageResult<SysTansterMoneyLogVo> findTransterMoneyList(SysTansterMoneyPageCo params) {
                log.error("findTransterMoneyList查询会员账变异常:{}", params, throwable);
                return new PageResult();
            }

            @Override
            public Result<List<SysTansterMoneyLogVo>> findAllByParent(SysTansterMoneyPageCo params) {
                log.error("findAllByParent根据父级查询转账记录异常:{}", params, throwable);
                return Result.failed("查询转账记录异常");
            }

            @GetMapping("/userWashCode/findUserWashCodeConfigList/{userId}")
            @Override
            public Result< List<UserWashCodeConfig>> findUserWashCodeConfigList(Long userId) {
                log.error("findUserWashCodeConfigList查询个人洗码配置异常:{}",userId,throwable);
                return Result.failed("查询失败");
            }

            @Override
            public Result saveUserWashCodeConfig( List<UserWashCodeConfigCo> list) {
                log.error("saveUserWashCodeConfig编辑个人洗码配置异常:{}",list,throwable);
                return Result.failed("编辑失败");
            }

            @Override
            public Result updateHeadImgUrl(String headImg) {
                log.error("updateHeadImgUrl error: {}", headImg);
                return null;
            }

            @Override
            public Result<UserInfoVo> findUserInfoById() {
                log.error("findUserInfoById error");
                return null;
            }

            @Override
            public Result<SysUserMoneyVo> getMoney() {
                log.error("getMoney error");
                return Result.failed("查询失败");
            }

            @Override
            public Result<SysUserMoneyVo> getMoneyByUserName(String userName) {
                log.error("getMoneyByUserName error,userName={}",userName);
                return Result.failed("查询失败");
            }

            @Override
            public Result<BigDecimal> getSumMoneyByParent(String parent) {
                log.error("getSumMoneyByParent 查询失败,parent={}", parent);
                return Result.failed("查询失败");
            }

            @Override
            public Result<String> receiveWashCode() {
                log.error("receiveWashCode error");
                return null;
            }

            @Override
            public Result<PageResult<WashCodeChangeVo>> getWashCodeRecord(String date) {
                log.error("getWashCodeRecord error: {}", date);
                return null;
            }

            @Override
            public Result<List<RoomFollowVo>> getRoomFollowList() {
                log.error("getRoomFollowList error");
                return null;
            }

            @Override
            public Result addFollow(Long roomId) {
                log.error("getWashCodeRecord error: {}", roomId);
                return null;
            }

            @Override
            public Result findUserNum(Map<String, Object> params) {
                log.error("findUserNum error");
                return Result.failed("查询失败");
            }
        };
    }
}
