package com.central.user.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysRole;
import com.central.user.feign.callback.UserServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author zlt
 */
@FeignClient(name = ServiceNameConstants.USER_SERVICE, fallbackFactory = UserServiceFallbackFactory.class, decode404 = true)
public interface RoleService {


    /**
     * 后台管理查询角色
     * @param params
     * @return
     */
    @GetMapping("/roles")
    PageResult<SysRole> findRoles(@RequestParam Map<String, Object> params);



    @GetMapping("/allRoles")
    Result<List<SysRole>> findAll() ;

    /**
     * 角色新增或者更新
     *
     * @param sysRole
     * @return
     */
    @PostMapping("/roles/saveOrUpdate")
    Result saveOrUpdate(@RequestBody SysRole sysRole) throws Exception ;

    /**
     * 后台管理删除角色
     * delete /role/1
     *
     * @param id
     */
    @DeleteMapping("/roles/{id}")
     Result deleteRole(@PathVariable("id")  Long id) ;
}
