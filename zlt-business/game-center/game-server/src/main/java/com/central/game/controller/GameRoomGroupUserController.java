package com.central.game.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.model.UserType;
import com.central.game.model.GameRoomGroupUser;
import com.central.game.model.co.GameRoomGroupCo;
import com.central.game.model.vo.GameRoomGroupUserVo;
import com.central.game.service.IGameRoomGroupUserService;
import com.central.user.feign.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/gameRoomGroupUser")
@Api(tags = "游戏房间用户分组")
public class GameRoomGroupUserController {

    @Autowired
    private IGameRoomGroupUserService gameRoomGroupUserService;
    @Autowired
    private UserService userService;

    @ApiOperation(value = "保存当前用户桌台分组")
    @PostMapping("/save")
    public Result save(@ModelAttribute GameRoomGroupCo co, @LoginUser SysUser sysUser) {
        SysUser sysUserFull = userService.selectByUsername(sysUser.getUsername());
        if (UserType.APP_GUEST.name().equals(sysUserFull.getType())) {
            return Result.failed("游客账号不支持此功能");
        }
        gameRoomGroupUserService.addGroup(co.getGameId(), co.getTableNum(), sysUser.getId());
        return Result.succeed();
    }

    @ApiOperation(value = "移除当前用户桌台分组")
    @PostMapping("/remove")
    public Result remove(@ModelAttribute GameRoomGroupCo co, @LoginUser SysUser sysUser) {
        SysUser sysUserFull = userService.selectByUsername(sysUser.getUsername());
        if (UserType.APP_GUEST.name().equals(sysUserFull.getType())) {
            return Result.failed("游客账号不支持此功能");
        }
        //先查询是否已存在
        GameRoomGroupUser user = gameRoomGroupUserService.checkExist(co.getGameId(), co.getTableNum(), sysUser.getId());
        if (user != null) {
            gameRoomGroupUserService.removeById(user.getId());
        }
        return Result.succeed();
    }

    @ApiOperation(value = "查询当前用户桌台分组列表")
    @GetMapping("/getTableNumGroupList")
    public Result<List<GameRoomGroupUserVo>> getTableNumGroupList(@ModelAttribute GameRoomGroupCo co, @LoginUser SysUser sysUser) {
        SysUser sysUserFull = userService.selectByUsername(sysUser.getUsername());
        if (UserType.APP_GUEST.name().equals(sysUserFull.getType())) {
            return Result.failed("游客账号不支持此功能");
        }
        List<GameRoomGroupUserVo> list = gameRoomGroupUserService.getTableNumGroupList(co.getGameId(), co.getTableNum(), sysUser.getId());
        return Result.succeed(list);
    }
}
