package com.central.platform.backend.controller;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysRole;
import com.central.user.feign.RoleService;
import com.central.user.model.co.RolePageCo;
import com.central.user.model.co.SysRoleCo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "角色中心")
@Slf4j
@RequestMapping("/platform/roles")
public class SysRolesController {

    @Resource
    private RoleService roleService;
    /**
     * 后台管理查询角色
     * @param params
     * @return
     */
    @ApiOperation(value = "后台管理查询角色列表")
    @GetMapping("/roles")
    public PageResult<SysRole> findRoles(@ModelAttribute RolePageCo params) {
        return roleService.findRoles(params);
    }




    /**
     * 用户管理查询所有角色
     * @return
     */
    @ApiOperation(value = "后台管理查询角色下拉框")
    @GetMapping("/allRoles")
    public Result<List<SysRole>> findAll() {
        return roleService.findAll();
    }
    /**
     * 角色新增或者更新
     *
     * @param sysRole
     * @return
     */
    @ApiOperation(value = "角色新增或者更新")
    @PostMapping("/roles/saveOrUpdate")
    public Result saveOrUpdate(@Valid @RequestBody SysRoleCo sysRole) throws Exception {
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
