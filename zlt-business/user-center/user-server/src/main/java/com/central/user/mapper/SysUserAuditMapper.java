package com.central.user.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.SysUserAudit;
import com.central.db.mapper.SuperMapper;
import com.central.user.model.co.SysUserAuditPageCo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserAuditMapper extends SuperMapper<SysUserAudit> {

    List<SysUserAudit> findList(Page<SysUserAudit> page, @Param("u")  SysUserAuditPageCo params);
}
