package com.central.platform.backend.service.impl;

import com.central.common.feign.UserService;
import com.central.common.model.PageResult2;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.platform.backend.service.SysTansterMoneyLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class SysTansterMoneyLogServiceImpl implements SysTansterMoneyLogService {

    @Resource
    private UserService userService;

    @Override
    public PageResult2<SysTansterMoneyLogVo> findList(Map<String, Object> params) {
        return userService.findTransterMoneyList(params);
    }
}
