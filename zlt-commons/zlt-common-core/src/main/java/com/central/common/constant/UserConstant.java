package com.central.common.constant;

import java.math.BigDecimal;

/**
 * 会员相关操作全局变量
 * 主要存储 会员redis Key, 会员下注订单， 上线分等和会员有关常量
 *
 */
public interface UserConstant {

    /**
     * 上下分最多只能 7 位数
     */
    BigDecimal maxTransterMoney = BigDecimal.valueOf(10000000);

    interface redisKey{
        String SYS_USER_MONEY_MONEY_LOCK = "sys_user_money_money_lock::";
        Integer WAIT_TIME = 120; //获取锁等待时间
        Integer LEASE_TIME = 60; //
    }
}
