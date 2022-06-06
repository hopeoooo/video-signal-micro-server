package com.central.game.service.impl;

import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.mapper.RoomFollowListMapper;
import com.central.game.model.GameRoomList;
import com.central.game.model.RoomFollowList;
import com.central.game.model.vo.GameRoomListVo;
import com.central.game.service.IGameRoomInfoOfflineService;
import com.central.game.service.IGameRoomListService;
import com.central.game.service.IRoomFollowListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户钱包表
 *
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
@Slf4j
@Service
public class RoomFollowListServiceImpl extends SuperServiceImpl<RoomFollowListMapper, RoomFollowList> implements IRoomFollowListService {

    @Autowired
    private RoomFollowListMapper roomFollowListMapper;
    @Autowired
    private IGameRoomInfoOfflineService gameRoomInfoOfflineService;
    @Autowired
    private IGameRoomListService gameRoomListService;

    @Override
    public List<GameRoomListVo> getRoomFollowList(Long userId) {
        List<GameRoomListVo> roomListVos = new ArrayList<>();
        List<GameRoomList> list = roomFollowListMapper.getRoomFollowList(userId);
        for (GameRoomList roomList : list) {
            GameRoomListVo vo = new GameRoomListVo();
            BeanUtils.copyProperties(roomList, vo);
            vo.setFollowStatus(1);
            vo.setRoomId(roomList.getId());
            vo.setTableNum(roomList.getGameRoomName());
            vo.setGameRoomName(roomList.getGameRoomName());
            gameRoomListService.setTabelInfo(vo);
            roomListVos.add(vo);
        }
        return roomListVos;
    }
}
