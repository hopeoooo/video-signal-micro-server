package com.central.user.component.initial;

import com.central.common.constant.CommonConstant;
import com.central.common.constant.SecurityConstants;
import com.central.common.model.*;
import com.central.config.dto.TouristDto;
import com.central.config.feign.ConfigService;
import com.central.user.service.ISysUserMoneyService;
import com.central.user.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 启动项目初始化游客数据
 */
@Component
@Slf4j
@Order(1)
public class GuestInitRunner implements CommandLineRunner {

    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysUserMoneyService sysUserMoneyService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("初始化游客");
        initGuestToDb();
        initGuestToRedis();
        log.info("游客初始化完成");
    }

    /**
     * 第一次启动项目初始化50个游客
     */
    public void initGuestToDb() {
        List<SysUser> sysUserList = userService.lambdaQuery().eq(SysUser::getType, UserType.APP_GUEST.name()).list();
        if (!CollectionUtils.isEmpty(sysUserList)) {
            return;
        }
        for (int i = 1; i < 51; i++) {
            SysUser sysUser = new SysUser();
            sysUser.setUsername("play" + i);
            sysUser.setNickname("play" + i);
            sysUser.setSex(0);
            sysUser.setType(UserType.APP_GUEST.name());
            sysUser.setHeadImgUrl(configService.avatarPictureInfo());
            try {
                Result result = userService.saveOrUpdateUser(sysUser);
                if (result.getResp_code() == CodeEnum.SUCCESS.getCode()) {
                    SysUserMoney sysUserMoney = new SysUserMoney();
                    sysUserMoney.setUserId(sysUser.getId());
                    Result<TouristDto> touristDtoResult = configService.findTouristAmount();
                    if (touristDtoResult.getResp_code() == CodeEnum.SUCCESS.getCode() && !ObjectUtils.isEmpty(touristDtoResult.getDatas())) {
                        sysUserMoney.setMoney(touristDtoResult.getDatas().getTouristAmount());
                    }
                    sysUserMoneyService.saveCache(sysUserMoney);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initGuestToRedis() {
        if (!redisTemplate.hasKey(CommonConstant.PLAYER_ACCOUNT_QUEUE)) {
            List<SysUser> sysUserList = userService.lambdaQuery().eq(SysUser::getType, UserType.APP_GUEST.name()).eq(SysUser::getEnabled, 1).list();
            sysUserList.forEach(item -> {
                //有可能多次初始化的时候刚好redis被用完，存的时候要判断游客是否在登录状态
                String loginKey = SecurityConstants.REDIS_UNAME_TO_ACCESS + CommonConstant.REDIS_WEBAPP + item.getUsername();
                if (!redisTemplate.hasKey(loginKey)) {
                    redisTemplate.opsForList().leftPush(CommonConstant.PLAYER_ACCOUNT_QUEUE, item.getUsername());
                }
            });
        }
    }
}
