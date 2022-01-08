
package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.common.model.SysNotice;
import com.central.config.feign.ConfigService;
import com.central.log.annotation.AuditLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


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

    @ResponseBody
    @GetMapping("/notice/findNoticeList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型(1:一般,2:维护,3:系统)", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "state", value = "状态(0:停用,1:启用)", required = false, dataType = "Integer")
    })
    public Result findNoticeList(@RequestParam Map<String, Object> params) {
        return configService.findNoticeList(params);
    }


    /**
     * 删除用户
     *
     * @param id
     */

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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "公告id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "state", value = "状态", required = true, dataType = "Boolean")
    })
    public Result updateEnabled(@RequestParam Map<String, Object> params) {
        return configService.updateEnabled(params);
    }


    /**
     * 新增or更新
     *
     * @param sysNotice
     * @return
     */

    @PostMapping("/notice/saveOrUpdate")
    @AuditLog(operation = "'新增或更新公告:' + #sysNotice.content")
    public Result saveOrUpdate(@RequestBody SysNotice sysNotice) throws Exception {
        return configService.saveOrUpdate(sysNotice);
    }


}
