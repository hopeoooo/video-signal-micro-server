package com.central.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.user.mapper.UserWashCodeConfigMapper;
import com.central.user.service.IUserWashCodeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@CacheConfig(cacheNames = {"userWashCodeConfig"})
public class UserWashCodeConfigServiceImpl extends SuperServiceImpl<UserWashCodeConfigMapper, UserWashCodeConfig> implements IUserWashCodeConfigService {


    @Override
    public UserWashCodeConfig findUserWashCodeConfigList(Long userId) {
        LambdaQueryWrapper<UserWashCodeConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWashCodeConfig::getUserId,userId);
        return baseMapper.selectOne(wrapper);
    }

    public Result saveCache(UserWashCodeConfig userWashCodeConfig) {
        boolean b = saveOrUpdate(userWashCodeConfig);
        return b ? Result.succeed(userWashCodeConfig, "操作成功") : Result.failed("操作失败");
    }

}
