package com.central.platform.backend.controller;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysRole;
import com.central.platform.backend.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Api(tags = "角色中心")
@Slf4j
@RequestMapping("/platform/roles")
public class SysRolesController {
    @Autowired
    private SysRoleService roleService;
    /**
     * 后台管理查询角色
     * @param params
     * @return
     */
    @ApiOperation(value = "后台管理查询角色")
    @GetMapping("/roles")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "name", value = "角色名称", required = false, dataType = "String")
    })
    public PageResult<SysRole> findRoles(@RequestParam Map<String, Object> params) {
        return roleService.findRoles(params);
    }

    /**
     * 角色新增或者更新
     *
     * @param sysRole
     * @return
     */
    @ApiOperation(value = "角色新增或者更新")
    @PostMapping("/roles/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SysRole sysRole) throws Exception {
        return roleService.saveOrUpdate(sysRole);
    }


    /**
     * 后台管理删除角色
     * delete /role/1
     *
     * @param id
     */
    @ApiOperation(value = "后台管理删除角色")
    @DeleteMapping("/roles/{id}")
    Result deleteRole(@PathVariable("id")  Long id) {
        return roleService.deleteRole(id);
    }

}
