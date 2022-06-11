package com.central.user.component.schedule;

import com.central.common.constant.CommonConstant;
import com.central.common.constant.SecurityConstants;
import com.central.common.model.SysUser;
import com.central.common.model.UserType;
import com.central.user.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ComparePlayers {

    @Autowired
    private ISysUserService appUserService;

    @Resource
    private RedisTemplate redisTemplate;

    //5分钟一次
    @Scheduled(cron = "0 0/5 * * * ?")
    public void scheduledTask() {
//        if (!redisTemplate.hasKey(CommonConstant.PLAYER_ACCOUNT_QUEUE)) {
//            return;
//        }
        List<SysUser> sysUserList = appUserService.lambdaQuery().eq(SysUser::getType, UserType.APP_GUEST.name()).eq(SysUser::getEnabled, 1).list();
        List<String> userNameList = sysUserList.stream().map(t -> t.getUsername()).collect(Collectors.toList());
        log.info("check player account");
        Long listSize = redisTemplate.opsForList().size(CommonConstant.PLAYER_ACCOUNT_QUEUE);
        List<Object> list = redisTemplate.opsForList().range(CommonConstant.PLAYER_ACCOUNT_QUEUE, 0, listSize - 1);
        log.info("++++++++  list size is {}", listSize);
        List<String> accList = userNameList.stream().filter(p -> !list.contains(p)).collect(Collectors.toList());
        log.info("accList is {}", accList);
        saveToRedis(accList);
    }


    private void saveToRedis(List<String> accList) {
        if (accList.size() > 0) {
            accList.forEach(item -> {
                String loginKey = SecurityConstants.REDIS_UNAME_TO_ACCESS + CommonConstant.REDIS_WEBAPP + item;
                if (!redisTemplate.hasKey(loginKey))
                    redisTemplate.opsForList().leftPush(CommonConstant.PLAYER_ACCOUNT_QUEUE, item);
            });
        }
    }
}
