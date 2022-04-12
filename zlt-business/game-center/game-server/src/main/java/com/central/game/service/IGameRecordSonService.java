package com.central.game.service;

import com.central.common.service.ISuperService;
import com.central.game.model.GameRecordSon;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
public interface IGameRecordSonService extends ISuperService<GameRecordSon> {

    void saveAddMoneyStatus(Long gameRecordId,Integer addMoneyStatus);
}

