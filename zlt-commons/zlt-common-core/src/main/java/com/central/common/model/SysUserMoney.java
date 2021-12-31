package com.central.common.model;

import com.central.common.model.SuperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户钱包表
 *
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_money")
public class SysUserMoney extends SuperEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 余额
     */
    private BigDecimal money;
    /**
     * 未完成流水
     */
    private BigDecimal unfinishedFlow;
    /**
     * 洗码
     */
    private BigDecimal washCode;
}
