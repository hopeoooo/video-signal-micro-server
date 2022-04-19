package com.central.game.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.game.model.GameRoomGroup;
import com.central.game.model.GameRoomGroupUser;
import com.central.game.model.GameRoomList;
import com.central.game.model.co.GameRecordLivePotCo;
import com.central.game.service.IGameRoomGroupService;
import com.central.game.service.IGameRoomGroupUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/gameRoomGroupUser")
@Api(tags = "游戏房间用户分组")
public class GameRoomGroupUserController {

    @Autowired
    private IGameRoomGroupService gameRoomGroupService;
    @Autowired
    private IGameRoomGroupUserService gameRoomGroupUserService;

    @ApiOperation(value = "保存当前用户桌台分组")
    @PostMapping("/save")
    public Result save(@ModelAttribute GameRecordLivePotCo co, @LoginUser SysUser sysUser) {
        //先查询是否已存在
        GameRoomGroupUser user = gameRoomGroupUserService.checkExist(co.getGameId(), co.getTableNum(), sysUser.getId());
        if (user != null) {
            return Result.succeed();
        }
        //查询未满的分组
        List<Long> groupList = gameRoomGroupUserService.getNotFullGroup(co.getGameId(), co.getTableNum());
        Long groupId = null;
        if (CollectionUtils.isEmpty(groupList)) {
            GameRoomGroup gameRoomGroup = new GameRoomGroup();
            gameRoomGroup.setGameId(co.getGameId());
            gameRoomGroup.setTableNum(co.getTableNum());
            gameRoomGroupService.save(gameRoomGroup);
            groupId = gameRoomGroup.getId();
        } else {
            groupId = groupList.get(0);
        }
        GameRoomGroupUser gameRoomGroupUser = new GameRoomGroupUser();
        gameRoomGroupUser.setUserId(sysUser.getId());
        gameRoomGroupUser.setGroupId(groupId);
        gameRoomGroupUserService.save(gameRoomGroupUser);
        return Result.succeed();
    }

}
