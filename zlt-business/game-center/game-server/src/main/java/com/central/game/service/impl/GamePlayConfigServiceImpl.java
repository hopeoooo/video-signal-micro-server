package com.central.game.service.impl;

import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.mapper.GamePlayConfigMapper;
import com.central.game.model.GamePlayConfig;
import com.central.game.service.IGamePlayConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@Service
public class GamePlayConfigServiceImpl extends SuperServiceImpl<GamePlayConfigMapper, GamePlayConfig> implements IGamePlayConfigService {
}
