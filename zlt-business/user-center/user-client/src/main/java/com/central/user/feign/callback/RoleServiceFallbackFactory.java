package com.central.user.feign.callback;

import com.central.common.model.PageResult2;
import com.central.common.model.Result;
import com.central.common.model.SysRole;
import com.central.user.feign.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;
import java.util.Map;


@Slf4j
public class RoleServiceFallbackFactory implements FallbackFactory<RoleService> {
    @Override
    public RoleService create(Throwable throwable) {
        return new RoleService() {
            @Override
            public PageResult2<SysRole> findRoles(Map<String, Object> params) {
                log.error("findRoles查询角色异常:{}", params, throwable);
                return new PageResult2();
            }

            @Override
            public Result<List<SysRole>> findAll() {
                log.error("findAll查询角色异常",  throwable);
                return Result.failed("查询角色失败");
            }

            @Override
            public Result saveOrUpdate(SysRole sysRole) throws Exception {
                log.error("saveOrUpdate编辑角色异常:{}", sysRole, throwable);
                return Result.failed("编辑角色失败");
            }

            @Override
            public Result deleteRole(Long id) {
                log.error("deleteRole删除角色异常:{}", id, throwable);
                return Result.failed("删除角色失败");
            }
        };
    }
}
