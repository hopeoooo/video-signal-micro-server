package com.central.common.feign.fallback;

import com.central.common.feign.RoleService;
import com.central.common.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Map;


@Slf4j
public class RoleServiceFallbackFactory implements FallbackFactory<RoleService> {
    @Override
    public RoleService create(Throwable throwable) {
        return new RoleService() {
            @Override
            public PageResult<SysRole> findRoles(Map<String, Object> params) {
                log.error("findRoles查询角色异常:{}", params, throwable);
                return new PageResult();
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
