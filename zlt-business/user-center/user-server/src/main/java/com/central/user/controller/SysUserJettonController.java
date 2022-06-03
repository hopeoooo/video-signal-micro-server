package com.central.user.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.model.SysUserJetton;
import com.central.user.service.ISysUserJettonService;
import com.central.user.model.vo.SysUserJettonVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(tags = "筹码配置模块")
@RequestMapping("/jetton")
public class SysUserJettonController {

    @Autowired
    private ISysUserJettonService sysUserJettonService;

    @GetMapping(value = "/uid")
    @ApiOperation(value = "根据uid查询用户筹码配置")
    public Result<String> queryJettonByUid(@LoginUser SysUser sysUser) {
        SysUserJetton sysUserJetton = sysUserJettonService.queryJettonByUid(sysUser.getId());
        String jettonConfig = sysUserJetton == null ? "5,10,20,50,100" : sysUserJetton.getJettonConfig();
        return Result.succeed(jettonConfig, "查询成功");
    }

    @PostMapping("/put_config")
    @ApiOperation(value = "设置用户筹码")
    public Result<Boolean> updateJettonConfig(@RequestBody SysUserJettonVO sysUserJettonVO, @LoginUser SysUser sysUser){
        sysUserJettonService.updateJettonConfig(sysUserJettonVO.getJetton_config(),sysUser.getId());
        return Result.succeed();
    }
}
