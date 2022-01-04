package com.central.platform.backend.service.impl;

import com.central.common.feign.UserService;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.platform.backend.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    public PageResult<SysUser> findSysUserList(Map<String, Object> params) {
        return userService.findSysUserList(params);
    }

    @Override
    public Result delete(Long id) {
        return userService.delete(id);
    }
}
