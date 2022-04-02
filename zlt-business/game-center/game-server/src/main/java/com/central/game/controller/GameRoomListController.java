package com.central.game.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.Result;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.GameRoomList;
import com.central.game.service.IGameRoomInfoOfflineService;
import com.central.game.service.IGameRoomListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/gameRoomList")
@Api(tags = "游戏房间列表")
public class GameRoomListController {

    @Autowired
    private IGameRoomListService iGameRoomListService;
    @Autowired
    private IGameRoomInfoOfflineService gameRoomInfoOfflineService;

    /**
     * 分页查询房间数据
     *
     * @param gameId
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "查询房间列表数据")
    @GetMapping("/findList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameId", value = "游戏Id", required = false, dataType = "Long")
    })
    public Result<List<GameRoomList>> findList(@RequestParam(value = "gameId",required = false) Long gameId) {
        List<GameRoomList> gameRoomList = iGameRoomListService.findGameRoomList(gameId);
        return Result.succeed(gameRoomList);
    }

    @ApiOperation(value = "根据游戏ID查询房间列表(前台用)")
    @GetMapping("/findRoomListByGameId/{gameId}")
    public Result<List<GameRoomList>> findRoomListByGameId(@PathVariable("gameId") Long gameId) {
        List<GameRoomList> gameRoomList = iGameRoomListService.lambdaQuery().eq(GameRoomList::getGameId, gameId).ne(GameRoomList::getRoomStatus, 0).list();
        //查询现场最新状态
        for (GameRoomList room : gameRoomList) {
            GameRoomInfoOffline roomInfoOffline = gameRoomInfoOfflineService.lambdaQuery().eq(GameRoomInfoOffline::getGameId, gameId).eq(GameRoomInfoOffline::getTableNum, room.getGameRoomName())
                    .orderByDesc(GameRoomInfoOffline::getCreateTime).last("limit 1").one();
            if (roomInfoOffline != null) {
                room.setStatus(roomInfoOffline.getStatus());
            }
        }
        return Result.succeed(gameRoomList);
    }

    @ApiOperation(value = "根据房间ID查询房间详情")
    @GetMapping("/findRoomDetailById/{id}")
    public Result<GameRoomList> findRoomDetailById(@PathVariable("id") Long id) {
        GameRoomList room = iGameRoomListService.getById(id);
        return Result.succeed(room);
    }

    @ApiOperation(value = "根据房间ids查询房间详情")
    @GetMapping("/findRoomDetailByIds/{ids}")
    public Result<List<GameRoomList>> findRoomDetailByIds(@PathVariable("ids") String ids) {
        if(StringUtils.isBlank(ids)){
            return Result.succeed();
        }
        String[] idArray = ids.split(",");
        LambdaQueryWrapper<GameRoomList> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(GameRoomList::getId, idArray);
        List<GameRoomList> roomList = iGameRoomListService.list(queryWrapper);
        return Result.succeed(roomList);
    }

    @ApiOperation(value = "新增/更新")
    @PostMapping("/save")
    public Result save(@RequestBody GameRoomList gameRoomList) {
          iGameRoomListService.saveOrUpdate(gameRoomList);
        return Result.succeed();
    }

    @ApiOperation(value = "根据ID修改房间状态")
    @PostMapping("/roomStatus/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roomStatus", value = "游戏房间状态 0禁用，1：正常，2：维护", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "maintainStart", value = "维护开始时间", required = false),
            @ApiImplicitParam(name = "maintainEnd", value = "维护结束时间", required = false),
    })
    public Result updateRoomStatus(@PathVariable Long id, @RequestParam("roomStatus") Integer roomStatus,
                                   @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") String maintainStart,
                                   @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") String maintainEnd) {
        if(roomStatus > 2 || roomStatus < 0){
            return Result.failed("参数不合法");
        }
        Boolean result = iGameRoomListService.updateRoomStatus(id, roomStatus,maintainStart,maintainEnd);
        if(result){
            return Result.succeed();
        }else{
            return Result.failed("修改房间状态失败");
        }
    }

    @ApiOperation(value = "根据ID删除")
    @DeleteMapping("/deleteById/{id}")
    public Result roomDeleteById(@PathVariable Long id) {
        iGameRoomListService.removeById(id);
        return Result.succeed();
    }
}
