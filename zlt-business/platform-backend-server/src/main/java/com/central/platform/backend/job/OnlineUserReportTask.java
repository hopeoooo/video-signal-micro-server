package com.central.platform.backend.job;

import com.central.common.constant.SecurityConstants;
import com.central.common.redis.template.RedisRepository;
import com.central.platform.backend.model.OnlineUser;
import com.central.platform.backend.service.IOnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

@Slf4j
@Component
public class OnlineUserReportTask {
    @Autowired
    private IOnlineUserService iOnlineUserService;

    @Autowired
    private RedisRepository redisRepository;

    private static String patten = "yyyy-MM-dd HH:mm";

    public static SimpleDateFormat getSimpleDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten);
        return simpleDateFormat;
    }

    @Scheduled(cron = "0 0/5 * * * ? ")
    public void begin(){
        String redisKey = SecurityConstants.REDIS_UNAME_TO_ACCESS+"online";
        Set<String> keySet = redisRepository.keys(redisKey+"*");
        Calendar nowTime = Calendar.getInstance();
        String format = getSimpleDateFormat().format(nowTime.getTime());
        OnlineUser onlineUser = new OnlineUser();
        onlineUser.setOnlineNum(keySet.size());
        onlineUser.setStaticsTimes(format);
        onlineUser.setStaticsDay(format.substring(0,10));
        iOnlineUserService.saveOnlineUser(onlineUser);
    }
}
