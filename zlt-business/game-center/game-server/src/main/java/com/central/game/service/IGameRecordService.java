package com.central.game.service;

import com.central.common.service.ISuperService;
import com.central.game.dto.GameRecordDto;
import com.central.game.model.GameList;
import com.central.game.model.GameRecord;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
public interface IGameRecordService extends ISuperService<GameRecord> {

    PageResult<GameRecord> findList(Map<String, Object> map);


    GameRecordDto findGameRecordTotal(Map<String, Object> map);
}

