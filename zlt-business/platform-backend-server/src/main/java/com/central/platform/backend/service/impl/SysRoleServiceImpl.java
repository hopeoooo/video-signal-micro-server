package com.central.platform.backend.service.impl;

import com.central.common.feign.RoleService;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysRole;
import com.central.platform.backend.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service
@Slf4j
public class SysRoleServiceImpl implements SysRoleService {
    @Resource
    private RoleService roleService;

    @Override
    public PageResult<SysRole> findRoles(Map<String, Object> params) {
        return roleService.findRoles(params);
    }

    @Override
    public Result saveOrUpdate(SysRole sysRole) throws Exception {
        return roleService.saveOrUpdate(sysRole);
    }

    @Override
    public Result deleteRole(Long id) {
        return roleService.deleteRole(id);
    }
}