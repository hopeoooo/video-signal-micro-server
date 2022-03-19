package com.central.game.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.utils.AddrUtil;
import com.central.game.model.co.GameRecordCo;
import com.central.game.service.IGameRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@RestController
@RequestMapping("/gameRecord")
@Api(tags = "游戏下注记录")
public class GameRecordController {

    @Autowired
    private IGameRecordService gameRecordService;

    @ApiOperation(value = "保存")
    @PostMapping("/save")
    public Result save(@Valid @RequestBody GameRecordCo co, @LoginUser SysUser user, HttpServletRequest request) {
        String ip = AddrUtil.getRemoteAddr(request);
        Result result = gameRecordService.saveRecord(co, user, ip);
        return result;
    }

}
