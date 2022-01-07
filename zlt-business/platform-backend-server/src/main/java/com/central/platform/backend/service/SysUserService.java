package com.central.platform.backend.service;

import com.central.common.model.PageResult;
import com.central.common.model.PageResult2;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface SysUserService {

        Result saveOrUpdate(SysUser sysUser);

        PageResult2<SysUser> findSysUserList(Map<String, Object> params);

        Result delete(Long id);

         Result updateEnabled(@RequestParam Map<String, Object> params) ;

        Result resetPassword(Long id);
}
