package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.dto.LoginLogPageDto;
import com.central.common.model.PageResult;
import com.central.common.model.SuperPage;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.dto.GameRecordDto;
import com.central.game.mapper.GameRecordMapper;
import com.central.game.model.GameList;
import com.central.game.model.GameRecord;
import com.central.game.service.IGameRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@Service
public class GameRecordServiceImpl extends SuperServiceImpl<GameRecordMapper, GameRecord> implements IGameRecordService {

    /**
     * 列表
     * @return
     */
    @Override
    public PageResult<GameRecord> findList(Map<String, Object> map){
        Page<GameRecord> page = new Page<>(MapUtils.getInteger(map, "page"),  MapUtils.getInteger(map, "limit"));
        List<GameRecord> list  =  baseMapper.findGameRecordList(page, map);
        return PageResult.<GameRecord>builder().data(list).count(page.getTotal()).build();
    }

    @Override
    public GameRecordDto findGameRecordTotal(Map<String, Object> map) {
      return   baseMapper.findGameRecordTotal(map);
    }

}
