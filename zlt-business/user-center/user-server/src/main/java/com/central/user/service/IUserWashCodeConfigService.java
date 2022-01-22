package com.central.user.service;

import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;
import com.central.common.service.ISuperService;

import java.util.List;

public interface IUserWashCodeConfigService extends ISuperService<UserWashCodeConfig> {

    List<UserWashCodeConfig> findUserWashCodeConfigList(Long userId);


    Result saveCache(List<UserWashCodeConfig> list) ;

    /**
     * 先查询用户级的配置，用户级没有再查全局
     * @param userId
     * @return
     */
    List<UserWashCodeConfig> findWashCodeConfigList(Long userId);
}
