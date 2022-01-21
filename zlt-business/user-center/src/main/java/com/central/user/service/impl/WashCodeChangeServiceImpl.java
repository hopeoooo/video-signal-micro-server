package com.central.user.service.impl;

import com.central.common.model.PageResult2;
import com.central.common.model.WashCodeChange;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.user.mapper.WashCodeChangeMapper;
import com.central.user.service.IWashCodeChangeService;
import com.central.user.vo.WashCodeChangeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WashCodeChangeServiceImpl extends SuperServiceImpl<WashCodeChangeMapper, WashCodeChange> implements IWashCodeChangeService {

    @Autowired
    private WashCodeChangeMapper mapper;

    @Override
    public PageResult2<WashCodeChangeVo> getWashCodeRecord(Long userId) {
        List<WashCodeChangeVo> list = mapper.getWashCodeRecord(userId);
        return PageResult2.<WashCodeChangeVo>builder().data(list).count((long)list.size()).build();
    }
}
