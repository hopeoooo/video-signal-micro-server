package com.central.common.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.feign.fallback.UserServiceFallbackFactory;
import com.central.common.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author zlt
 */
@FeignClient(name = ServiceNameConstants.USER_SERVICE, fallbackFactory = UserServiceFallbackFactory.class, decode404 = true)
public interface MenuService {



    @GetMapping("/menus/{roleId}/menus")
    List<Map<String, Object>> findMenusByRoleId(@PathVariable("roleId")   Long roleId);

    /**
     * 给角色分配菜单
     */
    @PostMapping("/menus/granted")
    Result setMenuToRole(@RequestBody SysMenu sysMenu);




    /**
     * 删除菜单
     *
     * @param id
     */
    @DeleteMapping("/menus/{id}")
     Result delete(@PathVariable("id")  Long id) ;

    /**
     * 查询所有菜单
     * @return
     */
    @GetMapping("/menus/findAlls")
     PageResult<SysMenu> findAlls() ;

    /**
     * 菜单管理：修改按钮获取菜单以及顶级菜单
     * @return
     */
    @GetMapping("/menus/findOnes")
     PageResult<SysMenu> findOnes() ;


    /**
     * 添加菜单 或者 更新
     *
     * @param menu
     * @return
     */
    @PostMapping("/menus/saveOrUpdate")
     Result saveOrUpdate(@RequestBody SysMenu menu) ;

}
