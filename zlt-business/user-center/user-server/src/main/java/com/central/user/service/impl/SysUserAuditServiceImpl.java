package com.central.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.*;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.user.mapper.SysUserAuditMapper;
import com.central.user.model.co.SysUserAuditPageCo;
import com.central.user.model.vo.SysUserAuditVo;
import com.central.user.service.ISysUserAuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SysUserAuditServiceImpl extends SuperServiceImpl<SysUserAuditMapper, SysUserAudit> implements ISysUserAuditService {

    @Autowired
    private SysUserAuditMapper sysUserAuditMapper;

    /**
     * 查询账变稽核记录
     *
     * @param params
     * @return
     */
    @Override
    public PageResult<SysUserAuditVo> findSysUserAuditList(SysUserAuditPageCo params) {
        Page<SysUserAudit> page = new Page<>(params.getPage(), params.getLimit());
        List<SysUserAudit> list = baseMapper.findList(page, params);

        List<SysUserAuditVo> sysUserAuditVoList = new ArrayList<>();
        for (SysUserAudit sysUserAudit : list) {
            SysUserAuditVo userAuditVo = new SysUserAuditVo();
            BeanUtils.copyProperties(sysUserAudit, userAuditVo);
            String orderTypeName = CapitalEnum.fingCapitalEnumType(sysUserAudit.getAuditType()).getName();
            userAuditVo.setOrderTypeName(orderTypeName);
            sysUserAuditVoList.add(userAuditVo);
        }
        long total = page.getTotal();
        return PageResult.<SysUserAuditVo>builder().data(sysUserAuditVoList).count(total).build();
    }
}
