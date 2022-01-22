package com.central.platform.backend.service;

import com.central.common.model.PageResult;
import com.central.common.vo.SysTansterMoneyLogVo;

import java.util.Map;

public interface SysTansterMoneyLogService {
    PageResult<SysTansterMoneyLogVo> findList(Map<String, Object> params);
}
