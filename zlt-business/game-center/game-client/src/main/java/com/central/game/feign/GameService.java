package com.central.game.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import com.central.game.feign.callback.GameServiceFallbackFactory;
import com.central.game.model.GameList;
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

}