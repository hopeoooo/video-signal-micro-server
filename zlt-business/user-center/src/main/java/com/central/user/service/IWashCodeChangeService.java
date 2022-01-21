package com.central.user.service;

import com.central.common.model.PageResult2;
import com.central.common.model.WashCodeChange;
import com.central.common.service.ISuperService;
import com.central.user.vo.WashCodeChangeVo;

import java.util.List;

public interface IWashCodeChangeService extends ISuperService<WashCodeChange> {

    PageResult2<WashCodeChangeVo> getWashCodeRecord(Long userId,String startTime,String endTime);
}

