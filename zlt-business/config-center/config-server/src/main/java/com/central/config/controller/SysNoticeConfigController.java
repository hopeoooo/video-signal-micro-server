package com.central.config.controller;

import com.central.common.model.Result;
import com.central.config.model.SysNotice;
import com.central.config.service.ISysNoticeService;
import com.central.log.annotation.AuditLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@Api(tags = "公告管理")
@RequestMapping("/notice")
public class SysNoticeConfigController {
    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 查询公告管理列表
     */
    @ApiOperation("查询公告管理列表")
    @ResponseBody
    @GetMapping("/findNoticeList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型(1:一般,2:维护,3:系统)", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "state", value = "状态(0:停用,1:启用)", required = false, dataType = "Integer")
    })
    public Result<List<SysNotice>> findNoticeList(@RequestParam Map<String, Object> params) {
        List<SysNotice> noticeList = noticeService.findNoticeList(params);
        return Result.succeed(noticeList,"查询成功");

    }

    @ApiOperation("查询公告列表(前台用)")
    @ResponseBody
    @GetMapping("/getNoticeList")
    public Result<List<SysNotice>> getNoticeList() {
        List<SysNotice> noticeList = noticeService.getNoticeList();
        return Result.succeed(noticeList);
    }
    /**
     * 删除
     *
     * @param id
     */
    @ApiOperation("删除公告")
    @DeleteMapping(value = "/deleteNoticeId/{id}")
    public Result deleteNoticeId(@PathVariable Long id) {
        //查询公告是否存在
        SysNotice sysNotice = noticeService.selectById(id);
        if (sysNotice==null){
            return  Result.failed("此公告不存在");
        }
        noticeService.delNoticeId(id);
        noticeService.syncPushNoticeToWebApp();
        return Result.succeed("删除成功");
    }

    /**
     * 修改公告状态
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "修改公告状态")
    @GetMapping("/updateEnabled")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "公告id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "state", value = "状态", required = true, dataType = "Boolean")
    })
    public Result updateEnabled(@RequestParam Map<String, Object> params) {
        Result result = noticeService.updateEnabled(params);
        noticeService.syncPushNoticeToWebApp();
        return result;
    }


    /**
     * 新增or更新
     *
     * @param sysNotice
     * @return
     */
    @ApiOperation(value = "新增or更新公告")
    @PostMapping("/saveOrUpdate")
    @AuditLog(operation = "'新增或更新公告:' + #sysNotice.content")
    public Result saveOrUpdate(@RequestBody SysNotice sysNotice) throws Exception {
        if (ObjectUtils.isEmpty(sysNotice.getContent()) ) {
            return Result.failed("必填项不允许为空");
        }
        Result result = noticeService.saveOrUpdateUser(sysNotice);
        noticeService.syncPushNoticeToWebApp();
        return result;
    }
}
