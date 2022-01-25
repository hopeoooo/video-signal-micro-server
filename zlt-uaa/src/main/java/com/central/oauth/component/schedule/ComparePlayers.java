package com.central.oauth.component.schedule;

import com.central.common.constant.SecurityConstants;
import com.central.common.model.SysUser;
import com.central.common.redis.template.RedisRepository;
import com.central.oauth.utils.ConstantPlayer;
import com.central.user.feign.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ComparePlayers {

    @Resource
    UserService userService;

    @Autowired
    private RedisRepository redisRepository;


//    @Scheduled(cron = "*/10 * * * * * ")
    public void scheduledTask(){
        if (!redisRepository.exists(ConstantPlayer.PLAYER_ACCOUNT_QUEUE)) return ;
        List<SysUser> sysUserList = userService.queryPlayerList();
        List<String> userNameList = sysUserList.stream().map(t->t.getUsername()).collect(Collectors.toList());
        log.info("check player account");
        Long listSize = redisRepository.opsForList().size(ConstantPlayer.PLAYER_ACCOUNT_QUEUE);
        List<Object> list = redisRepository.opsForList().range(ConstantPlayer.PLAYER_ACCOUNT_QUEUE,0,listSize-1);
        log.info("++++++++  list size is {}",listSize);
//        if(listSize/sysUserList.size()>0.5){
            List<String> accList = userNameList.stream().filter(p->!list.contains(p)).collect(Collectors.toList());
            log.info("accList is {}",accList);
            saveToRedis(accList);
//        }
    }

    private void saveToRedis(List<String> accList){
        if(accList.size()>0){
            accList.forEach(item ->{
                String loginKey = SecurityConstants.REDIS_UNAME_TO_ACCESS+ConstantPlayer.REDIS_WEBAPP+item;
                if(!redisRepository.exists(loginKey))
                    redisRepository.leftPush(ConstantPlayer.PLAYER_ACCOUNT_QUEUE,item);
            });

        }
    }
}
