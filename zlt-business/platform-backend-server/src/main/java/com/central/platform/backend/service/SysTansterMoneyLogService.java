package com.central.platform.backend.service;

import com.central.common.model.PageResult2;
import com.central.common.vo.SysTansterMoneyLogVo;

import java.util.Map;

public interface SysTansterMoneyLogService {
    PageResult2<SysTansterMoneyLogVo> findList(Map<String, Object> params);
}
