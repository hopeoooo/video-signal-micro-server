package com.central.platform.backend.service;

import com.central.common.model.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

public interface SysUserService {

    Result saveOrUpdate(SysUser sysUser);

    PageResult<SysUser> findSysUserList(Map<String, Object> params);

    Result delete(Long id);

    Result updateEnabled(@RequestParam Map<String, Object> params) ;

    Result resetPassword(Long id);

    Result updateVerify(Long id);

    Result<SysUserMoney> transterMoney(Long userId, BigDecimal money, String remark, Boolean transterType);

    LoginAppUser findByUsername(String username);

    Result updateGaKey(Map<String, Object> params);

    Result updateGaBind(Map<String, Object> params);
}
