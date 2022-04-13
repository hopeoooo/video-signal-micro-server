package com.central.game.service;

import com.central.common.service.ISuperService;
import com.central.game.model.WashCodeChange;
import com.central.user.model.vo.WashCodeChangeVo;

import java.util.List;

public interface IWashCodeChangeService extends ISuperService<WashCodeChange> {

    List<WashCodeChangeVo> getWashCodeRecord(Long userId, String startTime, String endTime);
}

