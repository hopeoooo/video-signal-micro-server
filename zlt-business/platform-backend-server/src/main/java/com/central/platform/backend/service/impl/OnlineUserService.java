package com.central.platform.backend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.PageResult2;
import com.central.platform.backend.mapper.OnlineUserMapper;
import com.central.platform.backend.model.OnlineUser;
import com.central.platform.backend.service.IOnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OnlineUserService implements IOnlineUserService {
    @Autowired
    private OnlineUserMapper onlineUserMapper;
    @Override
    public List<OnlineUser> findOnlineUserList(Map<String, Object> params) {
        return onlineUserMapper.findOnlineUserList(params);
    }

    @Override
    public PageResult2<OnlineUser> findPageList( Map<String, Object> params) {
        Page<OnlineUser> page = new Page<>(MapUtils.getInteger(params, "page"), MapUtils.getInteger(params, "limit"));
        List<OnlineUser> list = onlineUserMapper.findPageList(page, params);
        long total = page.getTotal();
        return PageResult2.<OnlineUser>builder().data(list).count(total).build();
    }

    @Override
    public int saveOnlineUser(OnlineUser onlineUser) {
        return onlineUserMapper.saveOnlineUser(onlineUser);
    }
}
