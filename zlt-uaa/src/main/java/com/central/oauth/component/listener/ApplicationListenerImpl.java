package com.central.oauth.component.listener;

import com.central.common.model.SysUser;
import com.central.common.redis.template.RedisRepository;
import com.central.oauth.utils.ConstantPlayer;
import com.central.user.feign.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class ApplicationListenerImpl implements ApplicationListener<ApplicationStartedEvent> {

    @Resource
    UserService userService;

    @Autowired
    private RedisRepository redisRepository;



    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("+++++++++   redis init players");
        List<SysUser> sysUsers = userService.queryPlayerList();
        log.info("{}",sysUsers);
        if(redisRepository.exists(ConstantPlayer.PLAYER_ACCOUNT_QUEUE)){

        }else{
            sysUsers.forEach(item ->{
                redisRepository.leftPush(ConstantPlayer.PLAYER_ACCOUNT_QUEUE,item.getUsername());
            });
        }


    }
}
