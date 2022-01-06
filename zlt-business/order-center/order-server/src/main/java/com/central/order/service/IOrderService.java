package com.central.order.service;

import com.central.common.model.PageResult2;
import com.central.order.model.Order;
import com.central.common.model.SuperPage;
import com.central.common.service.ISuperService;

/**
 * 订单表
 *
 * @author zlt
 * @date 2022-01-03 18:27:12
 */
public interface IOrderService extends ISuperService<Order> {
    /**
     * 列表
     * @param superPage
     * @return
     */
    PageResult2<Order> findList(SuperPage superPage);
}

