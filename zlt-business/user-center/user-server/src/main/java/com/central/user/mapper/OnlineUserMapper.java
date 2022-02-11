package com.central.user.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.user.model.co.OnlineUserCo;
import com.central.common.model.OnlineUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OnlineUserMapper {

    List<OnlineUser> findOnlineUserList(@Param("p") OnlineUserCo params);

    List<OnlineUser> findPageList(Page<OnlineUser> page, @Param("p") OnlineUserCo params);

    int saveOnlineUser(@Param("o") OnlineUser onlineUser);
}
