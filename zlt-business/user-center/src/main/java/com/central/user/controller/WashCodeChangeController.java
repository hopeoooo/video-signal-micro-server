package com.central.user.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.PageResult2;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.model.WashCodeChange;
import com.central.user.service.IWashCodeChangeService;
import com.central.user.vo.WashCodeChangeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/washCodeChange")
@Api(tags = "用户洗码明细")
public class WashCodeChangeController {

    @Autowired
    private IWashCodeChangeService washCodeChangeService;

    @ApiOperation(value = "查询登录用户洗码记录")
    @GetMapping("/getWashCodeRecord")
    public Result<PageResult2<WashCodeChangeVo>> getWashCodeRecord(@LoginUser SysUser user) {
        Long id = user.getId();
        PageResult2<WashCodeChangeVo> washCodeChangeList = washCodeChangeService.getWashCodeRecord(3L);
        return Result.succeed(washCodeChangeList);
    }

}
