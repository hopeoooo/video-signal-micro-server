package com.central.activity.controller;

import com.central.activity.model.FileCustom;
import com.central.activity.service.IFileService;
import com.central.common.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 活动模块
 */
@Slf4j
@RestController
@Api(tags = "活动模块api")
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private IFileService iFileService;

    @ApiOperation(value = "查询活动列表")
    @GetMapping("/test")
    public String list(){
        List<FileCustom> list = iFileService.fetchUnBackupFiles("text", 1);
        String data = JsonUtil.toJSONString(list);
        return data;
    }



}
