package com.central.game.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.annotation.LoginUser;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.game.model.GameRoomInfoOffline;
import com.central.game.model.GameRoomList;
import com.central.game.model.vo.GameRoomListVo;
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
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/gameRoomList")
@Api(tags = "游戏房间列表")
public class GameRoomListController {

    @Autowired
    private IGameRoomListService iGameRoomListService;
    @Autowired
    private IGameRoomInfoOfflineService gameRoomInfoOfflineService;

    @ResponseBody
    @ApiOperation(value = "修改状态")
    @GetMapping("/updateRoomStatus")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "time", value = "时间", required = false),
        @ApiImplicitParam(name = "roomStatus", value = "状态", required = false),
    })
    public Result updateRoomStatus(@RequestParam Map<String, Object> params) {
        iGameRoomListService.updateRoomStatus(params);
        return Result.succeed();
    }

    /**
     * 分页查询房间数据
     *
     * @param gameId
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "查询房间列表数据")
    @GetMapping("/findList")
    @ApiImplicitParams({@ApiImplicitParam(name = "gameId", value = "游戏Id", required = false, dataType = "Long")})
    public Result<List<GameRoomList>> findList(@RequestParam(value = "gameId", required = false) Long gameId) {
        List<GameRoomList> gameRoomList = iGameRoomListService.findGameRoomList(gameId);
        return Result.succeed(gameRoomList);
    }

    @ApiOperation(value = "根据游戏ID查询房间列表(前台用)")
    @GetMapping("/findRoomListByGameId/{gameId}")
    public Result<List<GameRoomListVo>> findRoomListByGameId(@PathVariable("gameId") Long gameId,
        @LoginUser SysUser sysUser) {
        List<GameRoomListVo> gameRoomList = iGameRoomListService.findRoomListByGameId(gameId, sysUser.getId());
        return Result.succeed(gameRoomList);
    }

    @ApiOperation(value = "根据房间ID查询房间详情")
    @GetMapping("/findRoomDetailById/{id}")
    public Result<GameRoomList> findRoomDetailById(@PathVariable("id") String id) {
        GameRoomList room = iGameRoomListService.findById(id);
        String i18nTableNum = iGameRoomListService.getI18nTableNum(room);
        room.setI18nTableNum(i18nTableNum);
        return Result.succeed(room);
    }

    @ApiOperation(value = "根据房间ids查询房间详情")
    @GetMapping("/findRoomDetailByIds/{ids}")
    public Result<List<GameRoomList>> findRoomDetailByIds(@PathVariable("ids") String ids) {
        if (StringUtils.isBlank(ids)) {
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
        Boolean result = false;
        if (Objects.isNull(gameRoomList.getId())) {
            result = iGameRoomListService.saveOrUpdate(gameRoomList);
        } else {
            result = iGameRoomListService.update(gameRoomList.getId(), gameRoomList);
        }
        if (result) {
            return Result.succeed();
        } else {
            return Result.failed("更新房间失败");
        }
    }

    @ApiOperation(value = "根据ID修改房间状态")
    @PostMapping("/roomStatus/{id}")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "roomStatus", value = "游戏房间状态 0禁用，1：正常，2：维护", required = false, dataType = "Integer"),
        @ApiImplicitParam(name = "maintainStart", value = "维护开始时间", required = false),
        @ApiImplicitParam(name = "maintainEnd", value = "维护结束时间", required = false),})
    public Result updateRoomStatus(@PathVariable Long id, @RequestParam("roomStatus") Integer roomStatus,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String maintainStart,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String maintainEnd) {
        if (roomStatus > 2 || roomStatus < 0) {
            return Result.failed("参数不合法");
        }
        Boolean result = iGameRoomListService.updateRoomStatus(id, roomStatus, maintainStart, maintainEnd);
        if (result) {
            return Result.succeed();
        } else {
            return Result.failed("修改房间状态失败");
        }
    }

    @ApiOperation(value = "根据ID删除")
    @DeleteMapping("/deleteById/{id}")
    public Result roomDeleteById(@PathVariable Long id) {
        Boolean result = iGameRoomListService.deleteBy(id);
        if (result) {
            return Result.succeed();
        } else {
            return Result.failed("删除房间失败");
        }
    }

    @ApiOperation(value = "根据房间游戏ID和名称查询房间详情")
    @GetMapping("/findByGameIdAndGameRoomName")
    public Result<GameRoomList> findByGameIdAndGameRoomName(@RequestParam("gameId") Long gameId,
        @RequestParam("gameRoomName") String gameRoomName) {
        GameRoomList room = iGameRoomListService.findByGameIdAndGameRoomName(gameId, gameRoomName);
        return Result.succeed(room);
    }
}
