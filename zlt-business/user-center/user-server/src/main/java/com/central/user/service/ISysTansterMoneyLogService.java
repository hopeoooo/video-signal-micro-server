package com.central.user.service;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysTansterMoneyLog;
import com.central.common.service.ISuperService;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.user.dto.UserTansterMoneyDto;
import com.central.user.model.co.SysTansterMoneyPageCo;

import java.util.List;
import java.util.Map;

public interface ISysTansterMoneyLogService extends ISuperService<SysTansterMoneyLog> {
    PageResult<SysTansterMoneyLogVo> findSysTansterMoneyList(SysTansterMoneyPageCo params);

    /**
     * 异步保存账变记录
     * @param sysTansterMoneyLog
     */
    void syncSave(SysTansterMoneyLog sysTansterMoneyLog);

    List<SysTansterMoneyLogVo> findAllByParent(SysTansterMoneyPageCo params);

    PageResult<SysTansterMoneyLogVo> findAmountChangeList(SysTansterMoneyPageCo params);

    List<UserTansterMoneyDto> findUserTansterMoneyDto(Map<String, Object> params);

     List<SysTansterMoneyLogVo>  findOrderTypeAccountChangeList( String orderType,List<Long> listId);
}
