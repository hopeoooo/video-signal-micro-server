package com.central.game.service.impl;

import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.mapper.GameRoomGroupMapper;
import com.central.game.model.GameRoomGroup;
import com.central.game.service.IGameRoomGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GameRoomGroupServiceImpl extends SuperServiceImpl<GameRoomGroupMapper, GameRoomGroup> implements IGameRoomGroupService {

}
