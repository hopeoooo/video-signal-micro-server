package com.central.user.service;

import com.central.common.model.PageResult;
import com.central.common.params.user.OnlineUserParams;
import com.central.common.model.OnlineUser;

import java.util.List;

public interface IOnlineUserService {

    List<OnlineUser> findOnlineUserList(OnlineUserParams params);

    List<OnlineUser> findOnlineUserMaps(Integer tag);

    PageResult<OnlineUser> findPageList(OnlineUserParams params);

    int saveOnlineUser(OnlineUser onlineUser);
}
