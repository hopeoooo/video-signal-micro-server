package com.central.platform.backend.service;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysRole;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

public interface SysRoleService {
    /**
     * 后台管理查询角色
     * @param params
     * @return
     */
  PageResult<SysRole> findRoles(@RequestParam Map<String, Object> params) ;

  Result saveOrUpdate(@RequestBody SysRole sysRole) throws Exception ;


    Result deleteRole(@PathVariable("id")  Long id) ;
}
