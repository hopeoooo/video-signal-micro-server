package com.central.platform.backend.service;

import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;

public interface IUserWashCodeConfigService  {

    Result<UserWashCodeConfig> findUserWashCodeConfigList(Long userId);


    Result saveUserWashCodeConfig(UserWashCodeConfig userWashCodeConfig) ;
}
