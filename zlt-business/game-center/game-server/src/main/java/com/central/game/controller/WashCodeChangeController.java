package com.central.game.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.utils.DateUtil;
import com.central.game.service.IWashCodeChangeService;
import com.central.user.model.vo.WashCodeChangeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", value = "时间：0：今天，1：昨天，2：近7天", required = true)
    })
    @GetMapping("/getWashCodeRecord")
    public Result<List<WashCodeChangeVo>> getWashCodeRecord(@LoginUser SysUser user, String date) {
        String startTime = null;
        String endTime = null;
        if ("1".equals(date)) {
            startTime = DateUtil.getStartTime(-1);
            endTime = DateUtil.getEndTime(-1);
        } else if ("2".equals(date)) {
            startTime = DateUtil.getStartTime(-7);
            endTime = DateUtil.getEndTime(0);
        } else {//默认查今天
            startTime = DateUtil.getStartTime(0);
            endTime = DateUtil.getEndTime(0);
        }
        List<WashCodeChangeVo> washCodeChangeList = washCodeChangeService.getWashCodeRecord(user.getId(),startTime,endTime);
        return Result.succeed(washCodeChangeList);
    }
}
