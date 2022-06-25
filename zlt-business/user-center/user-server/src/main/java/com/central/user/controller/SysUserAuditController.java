package com.central.user.controller;

import com.central.common.model.PageResult;
import com.central.user.model.co.SysUserAuditPageCo;
import com.central.user.model.vo.SysUserAuditVo;
import com.central.user.service.ISysUserAuditService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 稽核记录
 */
@Slf4j
@RestController
@RequestMapping("/userAudit")
@Api(tags = "稽核记录")
@Validated
public class SysUserAuditController {

    @Autowired
    private ISysUserAuditService iSysUserAuditService;

    @ResponseBody
    @GetMapping("/findUserAuditList")
    public PageResult<SysUserAuditVo> findUserAuditList(@Valid @ModelAttribute SysUserAuditPageCo params) {
        return iSysUserAuditService.findSysUserAuditList(params);
    }
}
