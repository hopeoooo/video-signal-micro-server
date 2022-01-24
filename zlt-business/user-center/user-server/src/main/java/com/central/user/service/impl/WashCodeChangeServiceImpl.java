package com.central.user.service.impl;

import com.central.common.model.PageResult;
import com.central.common.model.UserWashCodeConfig;
import com.central.common.model.WashCodeChange;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.user.mapper.WashCodeChangeMapper;
import com.central.user.service.IUserWashCodeConfigService;
import com.central.user.service.IWashCodeChangeService;
import com.central.user.vo.WashCodeChangeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WashCodeChangeServiceImpl extends SuperServiceImpl<WashCodeChangeMapper, WashCodeChange> implements IWashCodeChangeService {

    @Autowired
    private WashCodeChangeMapper mapper;
    @Autowired
    private IUserWashCodeConfigService userWashCodeConfigService;

    @Override
    public PageResult<WashCodeChangeVo> getWashCodeRecord(Long userId, String startTime, String endTime) {
        //查询最新的洗码返水配置
        List<UserWashCodeConfig> washCodeConfigList = userWashCodeConfigService.findWashCodeConfigList(userId);
        if (CollectionUtils.isEmpty(washCodeConfigList)) {
            return PageResult.<WashCodeChangeVo>builder().data(null).count(0L).build();
        }
        List<WashCodeChange> recordList = mapper.getWashCodeRecord(userId, startTime, endTime);
        List<WashCodeChangeVo> resultList = new ArrayList<>();
        for (UserWashCodeConfig washCodeConfig : washCodeConfigList) {
            WashCodeChangeVo vo = new WashCodeChangeVo();
            vo.setGameId(washCodeConfig.getGameId());
            vo.setGameName(washCodeConfig.getGameName());
            BigDecimal rate = washCodeConfig.getGameRate();
            BigDecimal gameRate = rate == null ? BigDecimal.ZERO.setScale(2) : rate.setScale(2, BigDecimal.ROUND_HALF_UP);
            vo.setRate(gameRate + "%");
            resultList.add(vo);
            for (WashCodeChange washCodeChange : recordList) {
                if (washCodeConfig.getGameId() == washCodeChange.getGameId()) {
                    vo.setAmount(washCodeChange.getAmount());
                    vo.setValidbet(washCodeChange.getValidbet());
                    break;
                }
            }
        }
        return PageResult.<WashCodeChangeVo>builder().data(resultList).count((long) resultList.size()).build();
    }
}
