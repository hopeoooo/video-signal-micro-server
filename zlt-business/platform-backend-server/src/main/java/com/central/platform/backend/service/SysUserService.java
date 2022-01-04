package com.central.platform.backend.service;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;

import java.util.Map;

public interface SysUserService {

        Result saveOrUpdate(SysUser sysUser);

        PageResult<SysUser> findSysUserList(Map<String, Object> params);

        Result delete(Long id);
}
