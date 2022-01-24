package com.central.game.controller;

import com.central.common.model.Result;
import com.central.game.model.GameRoomList;
import com.central.game.service.IGameRoomListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/gameRoomList")
@Api(tags = "游戏房间列表列表")
public class GameRoomListController {

    @Autowired
    private IGameRoomListService iGameRoomListService;

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
        List<GameRoomList> gameRoomList = iGameRoomListService.lambdaQuery().eq(GameRoomList::getGameId,gameId).ne(GameRoomList::getRoomStatus,0).list();
        return Result.succeed(gameRoomList);
    }

    @ApiOperation(value = "根据房间ID查询房间详情")
    @GetMapping("/findRoomDetailById/{id}")
    public Result<GameRoomList> findRoomDetailById(@PathVariable("id") Long id) {
        GameRoomList room = iGameRoomListService.getById(id);
        return Result.succeed(room);
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
            @ApiImplicitParam(name = "roomStatus", value = "游戏房间状态 0禁用，1：正常，2：维护", required = false, dataType = "Integer")
    })
    public Result updateRoomStatus(@PathVariable Long id, @RequestParam("roomStatus") Integer roomStatus) {
        if(roomStatus > 2 || roomStatus < 0){
            return Result.failed("参数不合法");
        }
        Boolean result = iGameRoomListService.updateRoomStatus(id, roomStatus);
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
