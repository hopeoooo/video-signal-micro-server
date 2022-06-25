package com.central.user.service;

import com.central.common.model.PageResult;
import com.central.common.model.SysUserAudit;
import com.central.common.service.ISuperService;
import com.central.user.model.co.SysUserAuditPageCo;
import com.central.user.model.vo.SysUserAuditVo;

public interface ISysUserAuditService extends ISuperService<SysUserAudit> {

    PageResult<SysUserAuditVo> findSysUserAuditList(SysUserAuditPageCo params);
}
