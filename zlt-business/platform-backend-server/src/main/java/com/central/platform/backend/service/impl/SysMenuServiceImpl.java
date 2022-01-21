package com.central.platform.backend.service.impl;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysMenu;
import com.central.platform.backend.service.SysMenuService;
import com.central.user.feign.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class SysMenuServiceImpl implements SysMenuService {

    @Resource
    private MenuService menuService;
    @Override
    public Result<List<Map<String, Object>>> findMenusByRoleId(Long roleId) {
        List<Map<String, Object>> menusByRoleId = menuService.findMenusByRoleId(roleId);
        return Result.succeed(menusByRoleId);
    }

    @Override
    public Result setMenuToRole(SysMenu sysMenu) {
        return menuService.setMenuToRole(sysMenu);
    }

    @Override
    public Result delete(Long id) {
        return menuService.delete(id);
    }

    @Override
    public Result<PageResult<SysMenu>> findAlls() {
        PageResult<SysMenu> alls = menuService.findAlls();
        return Result.succeed(alls);
    }

    @Override
    public Result saveOrUpdate(SysMenu menu) {
        return menuService.saveOrUpdate(menu);
    }
}
