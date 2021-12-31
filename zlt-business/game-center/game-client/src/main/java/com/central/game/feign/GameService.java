package com.central.game.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.game.feign.callback.GameServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

;

/**
 */
@FeignClient(name = ServiceNameConstants.GAME_SERVICE, fallbackFactory = GameServiceFallbackFactory.class, decode404 = true)
public interface GameService {
    /**
     * 查询游戏列表
     */
    @PostMapping(value = "/game/list")
    String list();
}