package com.central.game.feign.callback;

import com.central.game.feign.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 降级工场
 */
@Slf4j
public class GameServiceFallbackFactory implements FallbackFactory<GameService> {

    @Override
    public GameService create(Throwable cause) {
        return null;
    }
}
