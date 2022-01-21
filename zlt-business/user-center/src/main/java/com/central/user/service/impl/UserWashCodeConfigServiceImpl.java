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

import java.util.List;


@Slf4j
@Service
@CacheConfig(cacheNames = {"userWashCodeConfig"})
public class UserWashCodeConfigServiceImpl extends SuperServiceImpl<UserWashCodeConfigMapper, UserWashCodeConfig> implements IUserWashCodeConfigService {


    @Override
    public   List<UserWashCodeConfig> findUserWashCodeConfigList(Long userId) {
        LambdaQueryWrapper<UserWashCodeConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWashCodeConfig::getUserId,userId);
        List<UserWashCodeConfig> userWashCodeConfigs = baseMapper.selectList(wrapper);
        return userWashCodeConfigs;
    }

    public Result saveCache(List<UserWashCodeConfig> list) {
        boolean b = saveOrUpdateBatch(list);
        return b ? Result.succeed(list, "操作成功") : Result.failed("操作失败");
    }

}
