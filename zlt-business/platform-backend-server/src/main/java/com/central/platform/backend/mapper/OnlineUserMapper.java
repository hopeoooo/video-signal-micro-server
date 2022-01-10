package com.central.platform.backend.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.platform.backend.model.OnlineUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OnlineUserMapper {

    List<OnlineUser> findOnlineUserList(@Param("p") Map<String, Object> params);

    List<OnlineUser> findPageList(Page<OnlineUser> page, @Param("p") Map<String, Object> params);

    int saveOnlineUser(@Param("o") OnlineUser onlineUser);
}
