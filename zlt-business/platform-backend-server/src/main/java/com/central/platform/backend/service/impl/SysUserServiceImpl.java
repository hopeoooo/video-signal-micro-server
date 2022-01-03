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

    @Override
    public Result saveOrUpdate(SysUser sysUser) {
        return null;
    }

    @Override
    public PageResult<SysUser> findSysUserList(Map<String, Object> params) {
        return userService.findSysUserList(params);
    }
}
