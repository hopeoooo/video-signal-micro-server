package com.central.platform.backend.controller;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysMenu;
import com.central.user.feign.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@RestController
@Api(tags = "菜单管理")
@Slf4j
@RequestMapping("/platform/menu")
public class SysMenuController {

    @Resource
    private MenuService menuService;

    /**
     * 根据roleId获取对应的菜单
     * @param roleId
     * @return
     */
    @ApiOperation(value = "根据roleId获取对应的菜单")
    @GetMapping("/menus/{roleId}/menus")
    public Result<List<Map<String, Object>>> findMenusByRoleId(@PathVariable Long roleId) {
        return Result.succeed(menuService.findMenusByRoleId(roleId));
    }



    /**
     * 给角色分配菜单
     */
    @ApiOperation(value = "角色分配菜单")
    @PostMapping("/menus/granted")
    public Result setMenuToRole(@RequestBody SysMenu sysMenu) {
        return menuService.setMenuToRole(sysMenu);
    }


    /**
     * 删除菜单
     *
     * @param id
     */
    @ApiOperation(value = "删除菜单")
    @DeleteMapping("/menus/{id}")
    public Result delete(@PathVariable Long id) {
        return menuService.delete(id);
    }

    /**
     * 查询所有菜单
     *
     */
    @ApiOperation(value = "查询所有菜单")
    @GetMapping("/menus/findAlls")
    public Result<PageResult<SysMenu>> findAlls() {
        return Result.succeed(menuService.findAlls());
    }


    /**
     * 添加菜单 或者 更新
     *
     * @param menu
     * @return
     */
    @ApiOperation(value = "新增菜单")
    @PostMapping("/menussaveOrUpdate")
    public Result saveOrUpdate(@RequestBody SysMenu menu) {
        return menuService.saveOrUpdate(menu);
    }

}
