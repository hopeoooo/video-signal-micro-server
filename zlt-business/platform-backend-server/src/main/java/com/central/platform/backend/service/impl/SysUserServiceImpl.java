package com.central.platform.backend.service.impl;

import com.central.common.feign.UserService;
import com.central.common.model.*;
import com.central.platform.backend.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private UserService userService;

    /**
     * 新增或修改用户数据
     *
     * @param sysUser
     * @return
     */
    @Override
    public Result saveOrUpdate(SysUser sysUser) {
        return userService.saveOrUpdate(sysUser);
    }

    /**
     * 查询用户数据
     * @param params
     * @return
     */
    @Override
    public PageResult2<SysUser> findSysUserList(Map<String, Object> params) {
        return userService.findSysUserList(params);
    }

    /**
     * 物理删除用户
     *
     * @param id
     * @return
     */
    @Override
    public Result delete(Long id) {
        return userService.delete(id);
    }

    @Override
    public Result updateEnabled(Map<String, Object> params) {
        return userService.updateEnabled(params);
    }



    @Override
    public Result resetPassword(Long id) {
        return userService.resetPassword(id);
    }

    @Override
    public Result<SysUserMoney> transterMoney(Long userId, BigDecimal money, String remark, Boolean transterType) {
        return  userService.transterMoney(userId, money, remark, transterType);
    }
}
