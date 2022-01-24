package com.central.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.dto.LoginLogPageDto;
import com.central.common.model.LoginLog;
import com.central.common.model.PageResult;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.user.mapper.LoginLogMapper;
import com.central.user.service.ILoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
@CacheConfig(cacheNames = {"loginLog"})
public class LoginLogServiceImpl extends SuperServiceImpl<LoginLogMapper, LoginLog> implements ILoginLogService {

    @Override
    public PageResult<LoginLogPageDto> queryList(Map<String, Object> map) {
        Page<LoginLogPageDto> page = new Page<>(MapUtils.getInteger(map, "page"),  MapUtils.getInteger(map, "limit"));
        List<LoginLogPageDto> list  =  baseMapper.findAllLoginLog(page, map);
        return PageResult.<LoginLogPageDto>builder().data(list).count(page.getTotal()).build();
    }

}