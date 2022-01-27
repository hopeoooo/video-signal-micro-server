package com.central.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.annotation.LoginUser;
import com.central.common.model.CodeEnum;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.game.feign.GameService;
import com.central.game.model.GameRoomList;
import com.central.user.model.RoomFollowList;
import com.central.user.service.IRoomFollowListService;
import com.central.user.vo.RoomFollowVo;
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
    private GameService gameService;

    @ApiOperation(value = "当前登录用户新增房间关注")
    @PostMapping("/addFollow/{roomId}")
    public Result addFollow(@LoginUser SysUser user, @PathVariable("roomId") Long roomId) {
        Result<GameRoomList> result = gameService.findRoomDetailById(roomId);
        if (result.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return Result.failed("服务器异常,关注失败");
        }
        GameRoomList roomList = result.getDatas();
        if (roomList == null || roomList.getRoomStatus() == 0) {
            return Result.failed("当前房间不存在或已关闭,关注失败");
        }
        LambdaQueryWrapper<RoomFollowList> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(RoomFollowList::getUserId, user.getId());
        queryWrapper.eq(RoomFollowList::getRoomId, roomId);
        List<RoomFollowList> list = roomFollowListService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return Result.failed("当前房间已关注");

        }
        RoomFollowList roomFollowList = new RoomFollowList();
        roomFollowList.setUserId(user.getId());
        roomFollowList.setRoomId(roomId);
        roomFollowListService.save(roomFollowList);
        return Result.succeed();
    }

    @ApiOperation(value = "当前登录用户取消房间关注")
    @GetMapping("/removeFollow/{roomId}")
    public Result removeFollow(@LoginUser SysUser user, @PathVariable("roomId") Long roomId) {
        LambdaQueryWrapper<RoomFollowList> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(RoomFollowList::getUserId, user.getId());
        queryWrapper.eq(RoomFollowList::getRoomId, roomId);
        roomFollowListService.remove(queryWrapper);
        return Result.succeed();
    }

    @ApiOperation(value = "当前登录用户查询房间关注列表")
    @GetMapping("/getRoomFollowList")
    public Result<List<RoomFollowVo>> getRoomFollowList(@LoginUser SysUser user) {
        List<RoomFollowList> list = roomFollowListService.lambdaQuery().eq(RoomFollowList::getUserId, user.getId()).orderByDesc(RoomFollowList::getCreateTime).list();
        if (CollectionUtils.isEmpty(list)) {
            return Result.succeed();
        }
        //查询最新的房间状态，禁用的不显示
        StringJoiner joiner = new StringJoiner(",");
        for (RoomFollowList room : list) {
            joiner.add(room.getRoomId().toString());
        }
        String roomIds = joiner.toString();
        Result<List<GameRoomList>> detail = gameService.findRoomDetailByIds(roomIds);
        if (detail.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return Result.failed("服务器异常,查询失败");
        }
        List<RoomFollowVo> result = new ArrayList<>();
        for (RoomFollowList followList : list) {
            for (GameRoomList room : detail.getDatas()) {
                if (followList.getRoomId() == room.getId() && room.getRoomStatus() != 0) {
                    RoomFollowVo vo = new RoomFollowVo();
                    vo.setId(followList.getId());
                    vo.setRoomId(followList.getRoomId());
                    vo.setRoomName(room.getGameRoomName());
                    result.add(vo);
                }
            }
        }
        return Result.succeed(result);
    }
}
