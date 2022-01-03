package com.central.order.service.impl;

import org.springframework.stereotype.Service;
import com.central.common.model.PageResult;
import com.central.common.model.SuperPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.service.impl.SuperServiceImpl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import com.central.order.model.Order;
import com.central.order.mapper.OrderMapper;
import com.central.order.service.IOrderService;

/**
 * 订单表
 *
 * @author zlt
 * @date 2022-01-03 18:27:12
 */
@Slf4j
@Service
public class OrderServiceImpl extends SuperServiceImpl<OrderMapper, Order> implements IOrderService {
    /**
     * 列表
     * @param superPage
     * @return
     */
    @Override
    public PageResult<Order> findList(SuperPage superPage){
        Page<Order> page = new Page<>(superPage.getPage(), superPage.getLimit());
        List<Order> list  =  baseMapper.findList(page, superPage);
        return PageResult.<Order>builder().data(list).code(0).count(page.getTotal()).build();
    }
}
