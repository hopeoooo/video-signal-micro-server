package com.central.user.service;

import com.central.common.model.PageResult;
import com.central.common.model.WashCodeChange;
import com.central.common.service.ISuperService;
import com.central.user.vo.WashCodeChangeVo;

public interface IWashCodeChangeService extends ISuperService<WashCodeChange> {

    PageResult<WashCodeChangeVo> getWashCodeRecord(Long userId, String startTime, String endTime);
}

