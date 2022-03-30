package com.central.game.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.game.dto.GameRecordDto;
import com.central.game.dto.GameRecordReportDto;
import com.central.game.dto.HomeHistogramDto;
import com.central.game.dto.HomePageDto;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.GameRecord;
import com.central.game.model.co.*;
import com.central.game.model.co.GameListCo;
import com.central.game.model.co.GameRecordBetCo;
import com.central.game.model.co.GameRoomListCo;
import com.central.game.feign.callback.GameServiceFallbackFactory;
import com.central.game.model.GameList;
import com.central.game.model.GameRoomList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 */
@FeignClient(name = ServiceNameConstants.GAME_SERVICE, fallbackFactory = GameServiceFallbackFactory.class, decode404 = true)
public interface GameService {
    /**
     * 新增、更新
     */
    @PostMapping("/gamelist/save")
    Result save(@RequestBody GameListCo gameList);

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
    Result save(@RequestBody GameRoomListCo gameRoomList) ;

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

    /**
     * 根据房间ID查询房间详情
     * @param id
     * @return
     */
    @GetMapping("/gameRoomList/findRoomDetailById/{id}")
    Result<GameRoomList> findRoomDetailById(@PathVariable("id") Long id);

    /**
     * 根据房间IDs查询房间详情
     * @param ids
     * @return
     */
    @GetMapping("/gameRoomList/findRoomDetailByIds/{ids}")
    Result<List<GameRoomList>> findRoomDetailByIds(@PathVariable("ids") String ids);


    /**
     * 查询下注数据
     * @param params
     * @return
     */
    @GetMapping("/gameRecord/findList")
    Result<PageResult<GameRecord>> findList(@SpringQueryMap GameRecordBetCo params ) ;

    @GetMapping("/gameRecord/getGameRecordByParent")
    Result<List<GameRecord>> getGameRecordByParent(@SpringQueryMap GameRecordBetCo params ) ;

    @GetMapping("/gameLotteryResult/getLotteryResultList")
    Result<List<GameLotteryResult>> getLotteryResultList(@SpringQueryMap GameLotteryResultCo params ) ;


    @GetMapping("/gameLotteryResult/findList")
    Result<PageResult<GameLotteryResult>> findList(@SpringQueryMap GameLotteryResultBackstageCo params ) ;


    @GetMapping("/gameRecord/findGameRecordTotal")
    Result<GameRecordDto> findGameRecordTotal(@SpringQueryMap GameRecordBetCo params) ;

    @GetMapping("/gameRecord/findBetAmountTotal")
    Result<GameRecordReportDto> findBetAmountTotal(@RequestParam Map<String, Object> params) ;

    @GetMapping("/gameRecord/findValidbetTotal")
    Result<GameRecordReportDto> findValidbetTotal(@RequestParam Map<String, Object> params) ;

    @GetMapping("/gameRecord/findWinningAmountTotal")
    Result<GameRecordReportDto> findWinningAmountTotal(@RequestParam Map<String, Object> params) ;

    @GetMapping("/gameRecord/findHomePageDto")
    Result<HomePageDto> findHomePageDto(@RequestParam(value = "parent", required = false) String parent) ;

    @GetMapping("/gameRecord/findHomeHistogramDto")
    Result<HomeHistogramDto> findHomeHistogramDto(@RequestParam Map<String, Object> params) ;
}