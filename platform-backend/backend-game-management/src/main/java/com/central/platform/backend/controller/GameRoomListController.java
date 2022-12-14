package com.central.platform.backend.controller;

import com.central.common.model.Result;
import com.central.game.feign.GameService;
import com.central.game.model.GameRoomList;
import com.central.game.model.co.GameRoomListCo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/game")
@Api(tags = "游戏房间列表")
public class GameRoomListController {

    @Autowired
    private GameService gameService;

    /**
     * 分页查询房间数据
     *
     * @param gameId
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "查询房间列表数据")
    @GetMapping("/gameRoomList/findList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameId", value = "游戏Id", required = false, dataType = "Long")
    })
    public Result<List<GameRoomList>> findList(@RequestParam(value = "gameId", required = false)  Long gameId) {
        return gameService.findList(gameId);
    }

    @ApiOperation(value = "新增/更新")
    @PostMapping("/gameRoomList/save")
    public Result save(@RequestBody GameRoomListCo gameRoomList) {

        return gameService.save(gameRoomList);
    }

    @ApiOperation(value = "根据ID修改房间状态")
    @PostMapping("/gameRoomList/roomStatus/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roomStatus", value = "游戏房间状态 0禁用，1：正常，2：维护", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "maintainStart", value = "维护开始时间", required = false),
            @ApiImplicitParam(name = "maintainEnd", value = "维护结束时间", required = false),
    })
    public Result updateRoomStatus(@PathVariable Long id, @RequestParam("roomStatus") Integer roomStatus,
                                   @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") String maintainStart,
                                   @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") String maintainEnd) {
        return gameService.updateRoomStatus(id, roomStatus,maintainStart,maintainEnd);
    }

    @ApiOperation(value = "根据ID删除")
    @DeleteMapping("/gameRoomList/deleteById/{id}")
    public Result roomDeleteById(@PathVariable Long id) {
        return gameService.roomDeleteById(id);
    }
}
