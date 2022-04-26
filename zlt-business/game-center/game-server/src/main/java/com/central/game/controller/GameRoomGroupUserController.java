package com.central.game.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.game.model.co.GameRoomGroupCo;
import com.central.game.model.vo.GameRoomGroupUserVo;
import com.central.game.service.IGameRoomGroupUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/gameRoomGroupUser")
@Api(tags = "游戏房间用户分组")
public class GameRoomGroupUserController {

    @Autowired
    private IGameRoomGroupUserService gameRoomGroupUserService;

    @ApiOperation(value = "保存当前用户桌台分组")
    @PostMapping("/save")
    public Result save(@Valid @ModelAttribute GameRoomGroupCo co, @LoginUser SysUser sysUser) {
        gameRoomGroupUserService.addGroup(co.getGameId(), co.getTableNum(), sysUser);
        return Result.succeed();
    }

    @ApiOperation(value = "移除当前用户桌台分组")
    @PostMapping("/removeGroup")
    public Result removeGroup(@Valid @ModelAttribute GameRoomGroupCo co, @LoginUser SysUser sysUser) {
        gameRoomGroupUserService.removeGroup(co.getGameId(), co.getTableNum(), sysUser);
        return Result.succeed();
    }

    @ApiOperation(value = "查询当前用户桌台分组列表")
    @GetMapping("/getTableNumGroupList")
    public Result<List<GameRoomGroupUserVo>> getTableNumGroupList(@Valid @ModelAttribute GameRoomGroupCo co, @LoginUser SysUser sysUser) {
        List<GameRoomGroupUserVo> list = gameRoomGroupUserService.getTableNumGroupList(co.getGameId(), co.getTableNum(), sysUser);
        return Result.succeed(list);
    }

    @ApiOperation(value = "查询当前用户在的所有桌台", hidden = true)
    @GetMapping("/getAllGroupListByUserName/{userName}")
    public Result<List<GameRoomGroupUserVo>> getAllGroupListByUserName(@PathVariable String userName) {
        List<GameRoomGroupUserVo> list = gameRoomGroupUserService.getGroupList(userName);
        return Result.succeed(list);
    }

    @ApiOperation(value = "退出登录时退出桌台", hidden = true)
    @GetMapping("/removeAllGroup/{userName}")
    public Result removeAllGroup(@PathVariable String userName) {
        gameRoomGroupUserService.removeAllGroup(userName);
        return Result.succeed();
    }
}
