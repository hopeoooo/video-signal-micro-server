package com.central.user.service;

import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;
import com.central.common.service.ISuperService;

public interface IUserWashCodeConfigService extends ISuperService<UserWashCodeConfig> {

    UserWashCodeConfig findUserWashCodeConfigList(Long userId);


    Result saveCache(UserWashCodeConfig userWashCodeConfig) ;
}
