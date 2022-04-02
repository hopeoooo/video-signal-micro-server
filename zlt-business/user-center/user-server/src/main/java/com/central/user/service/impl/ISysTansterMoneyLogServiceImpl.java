package com.central.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.CapitalEnum;
import com.central.common.model.PageResult;
import com.central.common.model.SysTansterMoneyLog;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.common.utils.DateUtil;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.user.mapper.SysTansterMoneyLogMapper;
import com.central.user.model.co.SysTansterMoneyPageCo;
import com.central.user.service.ISysTansterMoneyLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ISysTansterMoneyLogServiceImpl extends SuperServiceImpl<SysTansterMoneyLogMapper, SysTansterMoneyLog> implements ISysTansterMoneyLogService {

    /**
     * 查询会员账变记录
     *
     * @param params
     * @return
     */
    @Override
    public PageResult<SysTansterMoneyLogVo> findSysTansterMoneyList(SysTansterMoneyPageCo params) {
        Page<SysTansterMoneyLog> page = new Page<>(params.getPage(), params.getLimit());
        List<SysTansterMoneyLog> list = baseMapper.findList(page, params);

        List<SysTansterMoneyLogVo> sysTansterMoneyLogs = new ArrayList<>();
        for (SysTansterMoneyLog sysTansterMoneyLog : list) {
            SysTansterMoneyLogVo sysTansterMoney = new SysTansterMoneyLogVo();
            BeanUtils.copyProperties(sysTansterMoneyLog, sysTansterMoney);
            String orderTypeName = CapitalEnum.fingCapitalEnumType(sysTansterMoneyLog.getOrderType()).name();
            sysTansterMoney.setOrderTypeName(orderTypeName);
            sysTansterMoneyLogs.add(sysTansterMoney);
        }
        long total = page.getTotal();
        return PageResult.<SysTansterMoneyLogVo>builder().data(sysTansterMoneyLogs).count(total).build();
    }


    @Override
    @Async
    public void syncSave(SysTansterMoneyLog sysTansterMoneyLog) {
        baseMapper.insert(sysTansterMoneyLog);
    }

    @Override
    public List<SysTansterMoneyLogVo> findAllByParent(SysTansterMoneyPageCo params) {
        List<SysTansterMoneyLogVo> list = baseMapper.findAllByParent(params);
        return list;
    }

    @Override
    public PageResult<SysTansterMoneyLogVo> findAmountChangeList(SysTansterMoneyPageCo params) {
        String startTime = DateUtil.getStartTime(0);
        String endTime = DateUtil.getEndTime(0);
        String type = params.getType();
        if ("1".equals(type)) {
            startTime = DateUtil.getSimpleDateFormat().format(DateUtil.getWeekStartDate());
            endTime = DateUtil.getSimpleDateFormat().format(DateUtil.getWeekEndDate());
        } else if ("2".equals(type)) {
            startTime = DateUtil.getLastWeekMonday();
            endTime = DateUtil.getLastWeekSunday();
        }
        params.setStartTime(startTime);
        params.setEndTime(endTime);
        PageResult<SysTansterMoneyLogVo> sysTansterMoneyList = findSysTansterMoneyList(params);
        return sysTansterMoneyList;
    }
}
