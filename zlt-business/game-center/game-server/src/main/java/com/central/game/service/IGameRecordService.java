package com.central.game.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.PageResult;
import com.central.common.model.SuperPage;
import com.central.common.service.ISuperService;
import com.central.game.dto.GameRecordDto;
import com.central.game.model.GameList;
import com.central.game.model.GameRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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

