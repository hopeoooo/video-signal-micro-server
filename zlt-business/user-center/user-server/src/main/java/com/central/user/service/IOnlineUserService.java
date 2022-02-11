package com.central.user.service;

import com.central.common.model.PageResult;
import com.central.user.model.co.OnlineUserCo;
import com.central.common.model.OnlineUser;

import java.util.List;

public interface IOnlineUserService {

    List<OnlineUser> findOnlineUserList(OnlineUserCo params);

    List<OnlineUser> findOnlineUserMaps(Integer tag);

    PageResult<OnlineUser> findPageList(OnlineUserCo params);

    int saveOnlineUser(OnlineUser onlineUser);
}
