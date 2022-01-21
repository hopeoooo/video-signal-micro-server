package com.central.platform.backend.service.impl;

import com.central.common.feign.UserService;
import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;
import com.central.platform.backend.service.IUserWashCodeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Service
public class UserWashCodeConfigServiceImpl implements IUserWashCodeConfigService {

    @Resource
    private UserService userService;
    @Override
    public Result<List<UserWashCodeConfig>> findUserWashCodeConfigList(Long userId) {
        return  userService.findUserWashCodeConfigList(userId);
    }

    @Override
    public Result saveUserWashCodeConfig( List<UserWashCodeConfig> list) {
        return userService.saveUserWashCodeConfig(list);
    }
}
