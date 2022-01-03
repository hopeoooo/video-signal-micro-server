package com.central.order.service.impl;

import org.springframework.stereotype.Service;
import com.central.common.model.PageResult;
import com.central.common.model.SuperPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.service.impl.SuperServiceImpl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import com.central.order.model.Orders;
import com.central.order.mapper.OrdersMapper;
import com.central.order.service.IOrdersService;

/**
 * 订单表
 *
 * @author zlt
 * @date 2022-01-03 18:27:12
 */
@Slf4j
@Service
public class OrdersServiceImpl extends SuperServiceImpl<OrdersMapper, Orders> implements IOrdersService {
    /**
     * 列表
     * @param superPage
     * @return
     */
    @Override
    public PageResult<Orders> findList(SuperPage superPage){
        Page<Orders> page = new Page<>(superPage.getPage(), superPage.getLimit());
        List<Orders> list  =  baseMapper.findList(page, superPage);
        return PageResult.<Orders>builder().data(list).code(0).count(page.getTotal()).build();
    }
}
