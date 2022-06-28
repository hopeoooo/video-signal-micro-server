package com.central.user.controller;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.user.model.co.AddUserAuditCo;
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
    @PostMapping("/findUserAuditList")
    public PageResult<SysUserAuditVo> findUserAuditList(@Valid @RequestBody SysUserAuditPageCo params) {
        return iSysUserAuditService.findSysUserAuditList(params);
    }

    /**
     * 增加打码量
     * @param params
     * @return
     */
    @ResponseBody
    @PostMapping("/addAudit")
    public Result addAudit(@Valid @RequestBody AddUserAuditCo params) {
        return iSysUserAuditService.addAudit(params);

    }

    /**
     * 解锁打码量
     *
     * @param params
     * @return
     */
    @ResponseBody
    @PostMapping("/subtractAudit")
    public Result subtractAudit(@Valid @RequestBody AddUserAuditCo params) {
        return iSysUserAuditService.subtractAudit(params);

    }
}
