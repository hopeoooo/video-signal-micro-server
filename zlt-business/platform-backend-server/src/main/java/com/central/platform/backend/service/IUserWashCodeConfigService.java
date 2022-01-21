package com.central.platform.backend.service;

import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;

import java.util.List;

public interface IUserWashCodeConfigService  {

    Result<List<UserWashCodeConfig>> findUserWashCodeConfigList(Long userId);


    Result saveUserWashCodeConfig( List<UserWashCodeConfig> list) ;
}
