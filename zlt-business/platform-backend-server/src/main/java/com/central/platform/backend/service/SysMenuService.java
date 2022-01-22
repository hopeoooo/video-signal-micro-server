package com.central.platform.backend.service;


import com.central.common.model.PageResult2;
import com.central.common.model.Result;
import com.central.common.model.SysMenu;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface SysMenuService {


    Result<List<Map<String, Object>>> findMenusByRoleId(@PathVariable Long roleId);

    /**
     * 给角色分配菜单
     */
    Result setMenuToRole(@RequestBody SysMenu sysMenu);




    /**
     * 删除菜单
     *
     * @param id
     */
    Result delete(@PathVariable Long id) ;

    /**
     * 查询所有菜单
     * @return
     */
    Result<PageResult2<SysMenu>> findAlls() ;

    /**
     * 添加菜单 或者 更新
     *
     * @param menu
     * @return
     */
    Result saveOrUpdate(@RequestBody SysMenu menu) ;
}
