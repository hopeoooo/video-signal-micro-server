package com.central.user.service;

import com.central.common.model.PageResult;
import com.central.common.model.SysTansterMoneyLog;
import com.central.common.service.ISuperService;
import com.central.common.vo.SysTansterMoneyLogVo;

import java.util.Map;

public interface ISysTansterMoneyLogService extends ISuperService<SysTansterMoneyLog> {
    PageResult<SysTansterMoneyLogVo> findSysTansterMoneyList(Map<String, Object> params);

    /**
     * 异步保存账变记录
     * @param sysTansterMoneyLog
     */
    void syncSave(SysTansterMoneyLog sysTansterMoneyLog);
}
