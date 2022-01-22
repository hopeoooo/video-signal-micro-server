package com.central.platform.backend.service;

import com.central.common.model.PageResult;
import com.central.platform.backend.model.OnlineUser;

import java.util.List;
import java.util.Map;

public interface IOnlineUserService {

    List<OnlineUser> findOnlineUserList(Map<String, Object> params);

    PageResult<OnlineUser> findPageList(Map<String, Object> params);

    int saveOnlineUser(OnlineUser onlineUser);
}
