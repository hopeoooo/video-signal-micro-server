package com.central.game.job;

import com.central.common.constant.SecurityConstants;
import com.central.common.redis.template.RedisRepository;
import com.central.game.model.GameRoomGroup;
import com.central.game.model.GameRoomGroupUser;
import com.central.game.model.vo.GameRoomGroupUserVo;
import com.central.game.service.IGameRoomGroupService;
import com.central.game.service.IGameRoomGroupUserService;
import com.central.game.service.IPushGameDataToClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时清除桌台用户
 */
@Component
@Slf4j
public class RemoveTableNumExpireUser {

    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private IGameRoomGroupUserService gameRoomGroupUserService;
    @Autowired
    private IPushGameDataToClientService pushGameDataToClientService;

    //每隔5分钟执行一次
    @Scheduled(cron = "0 0/5 * * * ?")
    public void scheduledTask() {
        List<GameRoomGroupUserVo> groupList = gameRoomGroupUserService.getGroupList(null);
        for (GameRoomGroupUserVo groupUser : groupList) {
            String onlineKey = SecurityConstants.REDIS_UNAME_TO_ACCESS + SecurityConstants.APP_USER_ONLINE + groupUser.getUserName();
            boolean exists = redisRepository.exists(onlineKey);
            if (!exists) {
                gameRoomGroupUserService.removeById(groupUser.getId());
                GameRoomGroupUserVo vo = new GameRoomGroupUserVo();
                vo.setGameId(groupUser.getGameId());
                vo.setTableNum(groupUser.getTableNum());
                vo.setStatus(0);
                vo.setUserName(groupUser.getUserName());
                pushGameDataToClientService.syncTableNumGroup(vo);
            }
        }
    }
}
