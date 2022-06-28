package com.central.user.service;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUserAudit;
import com.central.common.model.SysUserMoney;
import com.central.common.service.ISuperService;
import com.central.config.dto.BetMultipleDto;
import com.central.user.model.co.AddUserAuditCo;
import com.central.user.model.co.SysUserAuditPageCo;
import com.central.user.model.vo.SysUserAuditVo;

import java.math.BigDecimal;

public interface ISysUserAuditService extends ISuperService<SysUserAudit> {

    PageResult<SysUserAuditVo> findSysUserAuditList(SysUserAuditPageCo params);

    void saveAuditDate(SysUserMoney sysUserMoney, BigDecimal money, BigDecimal auditMultiple,
                       Integer transterType, SysUserMoney saveSysUserMoney, BetMultipleDto betMultipleDto, BigDecimal unFinishCode);

    Result addAudit(AddUserAuditCo params);

    Result subtractAudit(AddUserAuditCo params);
}
