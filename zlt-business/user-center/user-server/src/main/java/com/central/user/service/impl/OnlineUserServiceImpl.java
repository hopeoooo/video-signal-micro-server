package com.central.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.PageResult;
import com.central.common.params.user.OnlineUserParams;
import com.central.user.mapper.OnlineUserMapper;
import com.central.common.model.OnlineUser;
import com.central.user.service.IOnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OnlineUserServiceImpl implements IOnlineUserService {
    @Autowired
    private OnlineUserMapper onlineUserMapper;
    @Override
    public List<OnlineUser> findOnlineUserList(OnlineUserParams params) {
        return onlineUserMapper.findOnlineUserList(params);
    }

    @Override
    public PageResult<OnlineUser> findPageList(OnlineUserParams params) {
        Page<OnlineUser> page = new Page<>(params.getPage(), params.getLimit());
        List<OnlineUser> list = onlineUserMapper.findPageList(page, params);
        long total = page.getTotal();
        return PageResult.<OnlineUser>builder().data(list).count(total).build();
    }

    @Override
    public int saveOnlineUser(OnlineUser onlineUser) {
        return onlineUserMapper.saveOnlineUser(onlineUser);
    }
}
