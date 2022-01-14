package com.central.user.service.impl;

import com.central.common.model.SysTansterMoneyLog;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.user.mapper.SysTansterMoneyLogMapper;
import com.central.user.service.ISysTansterMoneyLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ISysTansterMoneyLogServiceImpl extends SuperServiceImpl<SysTansterMoneyLogMapper, SysTansterMoneyLog> implements ISysTansterMoneyLogService {
}
