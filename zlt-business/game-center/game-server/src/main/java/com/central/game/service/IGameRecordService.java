package com.central.game.service;

import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.service.ISuperService;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameRecordCo;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
public interface IGameRecordService extends ISuperService<GameRecord> {

    Result saveRecord(GameRecordCo co, SysUser user, String ip);
}

