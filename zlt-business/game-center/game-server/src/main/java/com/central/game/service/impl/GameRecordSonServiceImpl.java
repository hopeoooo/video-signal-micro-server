package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.mapper.GameRecordSonMapper;
import com.central.game.model.GameRecordSon;
import com.central.game.service.IGameRecordSonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@Service
public class GameRecordSonServiceImpl extends SuperServiceImpl<GameRecordSonMapper, GameRecordSon> implements IGameRecordSonService {
}
