package com.central.game.service.impl;

import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.mapper.MqConsumerErrorMsgMapper;
import com.central.game.model.MqConsumerErrorMsg;
import com.central.game.service.IMqConsumerErrorMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@Service
public class MqConsumerErrorMsgServiceImpl extends SuperServiceImpl<MqConsumerErrorMsgMapper, MqConsumerErrorMsg> implements IMqConsumerErrorMsgService {
}
