package com.central.user.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.model.SysUserJetton;
import com.central.user.service.ISysUserJettonService;
import com.central.user.vo.SysUserJettonVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
    public Result<String> queryJettonByUid(@LoginUser SysUser sysUser){

        SysUserJetton sysUserJetton = sysUserJettonService.lambdaQuery().eq(SysUserJetton::getUid,sysUser.getId()).one();
        return Result.succeed(sysUserJetton == null?"5,10,20,50,100": sysUserJetton.getJettonConfig(),"query success");
    }

    @PostMapping("/put_config")
    @ApiOperation(value = "设置用户筹码")
    public Result<Boolean> updateJettonConfig(@RequestBody SysUserJettonVO sysUserJettonVO, @LoginUser SysUser sysUser){
        SysUserJetton dbSysjetton = sysUserJettonService.lambdaQuery().eq(SysUserJetton::getUid,sysUser.getId()).one();
        Boolean result = Boolean.FALSE;
        if(dbSysjetton == null){
            SysUserJetton sysUserJetton = SysUserJetton.builder().uid(sysUser.getId()).jettonConfig(sysUserJettonVO.getJetton_config()).build();
            result = sysUserJettonService.save(sysUserJetton);
        }else {
            dbSysjetton.setJettonConfig(sysUserJettonVO.getJetton_config());
            result = sysUserJettonService.updateById(dbSysjetton);
        }
        return Result.succeed(result);
    }
}
