
package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.config.feign.ConfigService;
import com.central.config.model.SysNotice;
import com.central.config.model.co.FindNoticeCo;
import com.central.config.model.co.SysNoticeCo;
import com.central.config.model.co.UpdateNoticeCo;
import com.central.log.annotation.AuditLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@RestController
@Api(tags = "公告管理")
@RequestMapping("/notice/backend")
public class SysNoticeConfigController {
    @Resource
    private ConfigService configService;


    /**
     * 查询公告管理列表
     */
    @ApiOperation(value = "查询公告管理列表")
    @ResponseBody
    @GetMapping("/notice/findNoticeList")
    public Result<List<SysNotice>> findNoticeList(@ModelAttribute FindNoticeCo params) {
        return configService.findNoticeList(params);
    }


    /**
     * 删除
     *
     * @param id
     */
    @ApiOperation(value = "删除公告")
    @DeleteMapping(value = "/notice/deleteNoticeId/{id}")
    public Result deleteNoticeId(@PathVariable Long id) {
        return  configService.deleteNoticeId(id);
    }


    /**
     * 修改公告状态
     *
     * @param params
     * @return
     */

    @ApiOperation(value = "修改公告状态")
    @GetMapping("/notice/updateEnabled")
    public Result updateEnabled(@ModelAttribute UpdateNoticeCo params) {
        return configService.updateEnabled(params);
    }


    /**
     * 新增or更新
     *
     * @param sysNotice
     * @return
     */
    @ApiOperation(value = "新增或更新")
    @PostMapping("/notice/saveOrUpdate")
    @AuditLog(operation = "'新增或更新公告:' + #sysNotice.content")
    public Result saveOrUpdate(@RequestBody SysNoticeCo sysNotice) throws Exception {
        return configService.saveOrUpdate(sysNotice);
    }


}

