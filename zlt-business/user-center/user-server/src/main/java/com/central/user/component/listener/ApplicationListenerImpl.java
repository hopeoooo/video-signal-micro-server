package com.central.user.component.listener;

import com.central.common.constant.CommonConstant;
import com.central.common.model.SysUser;
import com.central.user.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class ApplicationListenerImpl implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private ISysUserService appUserService;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("+++++++++   redis init players");
        List<SysUser> sysUserList = appUserService.lambdaQuery().eq(SysUser::getType, CommonConstant.USER_TYPE_APP_GUEST).list();
        log.info("{}",sysUserList);
        if(!redisTemplate.hasKey(CommonConstant.PLAYER_ACCOUNT_QUEUE)){
            sysUserList.forEach(item ->{
                redisTemplate.opsForList().leftPush(CommonConstant.PLAYER_ACCOUNT_QUEUE,item.getUsername());
            });
        }
    }
}
