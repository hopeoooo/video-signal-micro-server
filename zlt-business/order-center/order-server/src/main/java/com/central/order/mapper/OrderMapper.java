package com.central.order.mapper;

import com.central.order.model.Order;
import com.central.common.model.SuperPage;
import com.central.db.mapper.SuperMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单表
 * 
 * @author zlt
 * @date 2022-01-03 18:27:12
 */
@Mapper
public interface OrderMapper extends SuperMapper<Order> {
    /**
     * 分页查询用户列表
     * @param page
     * @param superPage
     * @return
     */
    List<Order> findList(Page<Order> page, @Param("sp")SuperPage superPage);
}
