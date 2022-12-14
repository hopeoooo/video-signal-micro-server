package com.central.game.service.impl;

import com.central.common.constant.I18nKeys;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.common.utils.DateUtil;
import com.central.common.utils.ServletUtil;
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
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
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
        List<GameRoomListVo> list = roomFollowListMapper.getRoomFollowList(userId);
        for (GameRoomListVo vo : list) {
            //判断游戏状态
            if (!ObjectUtils.isEmpty(vo.getGameStatus()) && vo.getGameStatus() == 2) {
                boolean maintain = DateUtil.isEffectiveDate(new Date(), vo.getGameMaintainStart(), vo.getGameMaintainEnd());
                //当前时间不在维护时间区间内属于正常状态
                if (!maintain) {
                    vo.setGameStatus(1);
                    vo.setGameMaintainStart(null);
                    vo.setGameMaintainEnd(null);
                }
            }
            vo.setFollowStatus(1);
            vo.setRoomId(vo.getId());
            vo.setTableNum(vo.getGameRoomName());
            String i18nTableNum = getI18nTableNum(vo);
            vo.setI18nTableNum(i18nTableNum);
            gameRoomListService.setTabelInfo(vo);
            roomListVos.add(vo);
        }
        return roomListVos;
    }

    public String getI18nTableNum(GameRoomListVo roomList) {
        if (roomList == null) {
            return null;
        }
        //多语言转化
        HttpServletRequest request = ServletUtil.getHttpServletRequest();
        String language = request.getHeader(I18nKeys.LANGUAGE);
        if (I18nKeys.Locale.ZH_CN.equalsIgnoreCase(language)) {
            return roomList.getGameRoomName();
        } else if (I18nKeys.Locale.KHM.equalsIgnoreCase(language)) {
            return roomList.getKhmName();
        } else if (I18nKeys.Locale.TH.equalsIgnoreCase(language)) {
            return roomList.getThName();
        }
        return roomList.getEnName();
    }
}
