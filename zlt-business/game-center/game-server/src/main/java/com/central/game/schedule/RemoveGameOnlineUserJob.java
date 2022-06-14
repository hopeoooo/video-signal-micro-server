package com.central.game.schedule;

import com.central.common.constant.SecurityConstants;
import com.central.common.redis.template.RedisRepository;
import com.central.game.model.vo.GameRoomGroupUserVo;
import com.central.game.service.IGameRoomGroupUserService;
import com.central.game.service.IPushGameDataToClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 每5分钟移除一次游戏在线登录过期的用户
 */
@Slf4j
@Component
public class RemoveGameOnlineUserJob implements SimpleJob {

    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private IGameRoomGroupUserService gameRoomGroupUserService;
    @Autowired
    private IPushGameDataToClientService pushGameDataToClientService;

    @Override
    public void execute(ShardingContext shardingContext) {
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
