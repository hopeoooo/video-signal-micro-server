package com.central.game.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import com.central.common.vo.SysMoneyVO;
import com.central.game.feign.callback.GameServiceFallbackFactory;
import com.central.game.model.GameList;
import com.central.game.model.GameRoomList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 */
@FeignClient(name = ServiceNameConstants.GAME_SERVICE, fallbackFactory = GameServiceFallbackFactory.class, decode404 = true)
public interface GameService {
    /**
     * 新增、更新
     */
    @PostMapping("/gamelist/save")
    Result save(@RequestBody GameList gameList);

    /**
     * 查询全部游戏
     * @return
     */
    @GetMapping("/gamelist/findAll")
    Result<List<GameList>> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @GetMapping("/gamelist/findById/{id}")
    Result<GameList> findById(@PathVariable("id") Long id);

    /**
     * 根据ID删除
     * @param id
     * @return
     */
    @DeleteMapping("/gamelist/deleteById/{id}")
    Result deleteById(@PathVariable("id") Long id);

    @GetMapping("/gamelist/findAllOpenRate")
    Result<List<GameList>> findAllOpenRate();

    @GetMapping("/gamelist/findGameList")
     Result<List<GameList>> findGameList(@RequestParam("state") Integer state) ;

    /**
     * 房间列表
     *
     * @param gameId
     * @return
     */
    @GetMapping("/gameRoomList/findList")
    Result<List<GameRoomList>> findList(@RequestParam("gameId") Long gameId) ;

    /**
     * 新增或者修改房间
     *
     * @param gameRoomList
     * @return
     */
    @PostMapping("/gameRoomList/save")
    Result save(@RequestBody GameRoomList gameRoomList) ;

    /**
     * 修改房间状态
     *
     * @param id
     * @param roomStatus
     * @return
     */
    @PostMapping("/gameRoomList/roomStatus/{id}")
    Result updateRoomStatus(@PathVariable("id") Long id, @RequestParam("roomStatus") Integer roomStatus
            ,@RequestParam("maintainStart") String maintainStart,@RequestParam("maintainEnd")String maintainEnd) ;

    /**
     * 根据ID删除房间
     * @param id
     * @return
     */
    @DeleteMapping("/gameRoomList//deleteById/{id}")
    Result roomDeleteById(@PathVariable("id") Long id);
}