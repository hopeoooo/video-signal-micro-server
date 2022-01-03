package com.central.order.service;

import com.central.order.model.Orders;
import com.central.common.model.PageResult;
import com.central.common.model.SuperPage;
import com.central.common.service.ISuperService;

/**
 * 订单表
 *
 * @author zlt
 * @date 2022-01-03 18:27:12
 */
public interface IOrdersService extends ISuperService<Orders> {
    /**
     * 列表
     * @param superPage
     * @return
     */
    PageResult<Orders> findList(SuperPage superPage);
}

