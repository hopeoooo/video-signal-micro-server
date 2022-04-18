package com.central.game.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.annotation.LoginUser;
import com.central.common.model.CodeEnum;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.game.model.GameRoomList;
import com.central.game.model.RoomFollowList;
import com.central.game.model.vo.GameRoomListVo;
import com.central.game.service.IGameRoomListService;
import com.central.game.service.IRoomFollowListService;
import com.central.user.model.vo.RoomFollowVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * 用户钱包表
 *
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
@Slf4j
@RestController
@RequestMapping("/followList")
@Api(tags = "房间关注列表")
public class RoomFollowListController {
    @Autowired
    private IRoomFollowListService roomFollowListService;
    @Autowired
    private IGameRoomListService gameRoomListService;

    @ApiOperation(value = "当前登录用户关注、取消关注房间")
    @PostMapping("/addOrRemoveFollow/{roomId}")
    public Result addFollow(@LoginUser SysUser user, @PathVariable("roomId") Long roomId) {
        GameRoomList roomList = gameRoomListService.getById(roomId);
        if (roomList == null || roomList.getRoomStatus() == 0) {
            return Result.failed("当前房间不存在或已关闭,操作失败");
        }
        LambdaQueryWrapper<RoomFollowList> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(RoomFollowList::getUserId, user.getId());
        queryWrapper.eq(RoomFollowList::getRoomId, roomId);
        List<RoomFollowList> list = roomFollowListService.list(queryWrapper);
        //存在就取消关注
        if (CollectionUtils.isNotEmpty(list)) {
            roomFollowListService.remove(queryWrapper);
            return Result.succeed("取消关注成功");
        }
        RoomFollowList roomFollowList = new RoomFollowList();
        roomFollowList.setUserId(user.getId());
        roomFollowList.setRoomId(roomId);
        roomFollowListService.save(roomFollowList);
        return Result.succeed("关注成功");
    }

    @ApiOperation(value = "当前登录用户查询房间关注列表")
    @GetMapping("/getRoomFollowList")
    public Result<List<GameRoomListVo>> getRoomFollowList(@LoginUser SysUser user) {
        List<GameRoomListVo> roomFollowList = roomFollowListService.getRoomFollowList(user.getId());
        return Result.succeed(roomFollowList);
    }

    @ApiOperation(value = "清空游客关注列表",hidden = true)
    @GetMapping("/clearGuestFollowList/{userId}")
    public Result clearGuestFollowList(@PathVariable Long userId) {
        LambdaQueryWrapper<RoomFollowList> lqw = Wrappers.lambdaQuery();
        lqw.eq(RoomFollowList::getUserId,userId);
        roomFollowListService.remove(lqw);
        return Result.succeed();
    }
}
