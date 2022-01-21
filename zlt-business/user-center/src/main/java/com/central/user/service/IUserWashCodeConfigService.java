package com.central.user.service;

import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;
import com.central.common.service.ISuperService;

import java.util.List;

public interface IUserWashCodeConfigService extends ISuperService<UserWashCodeConfig> {

    List<UserWashCodeConfig> findUserWashCodeConfigList(Long userId);


    Result saveCache(List<UserWashCodeConfig> list) ;
}
