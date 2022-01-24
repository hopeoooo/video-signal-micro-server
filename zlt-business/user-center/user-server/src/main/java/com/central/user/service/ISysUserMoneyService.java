package com.central.user.service;

import com.central.common.model.PageResult;
import com.central.common.model.SysUser;
import com.central.common.model.SysUserMoney;
import com.central.common.service.ISuperService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 用户钱包表
 *
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
public interface ISysUserMoneyService extends ISuperService<SysUserMoney> {
    /**
     * 列表
     * @param params
     * @return
     */
    PageResult<SysUserMoney> findList(Map<String, Object> params);

    SysUserMoney findByUserId(Long userId);

    SysUserMoney saveCache(SysUserMoney sysUserMoney);

    SysUserMoney transterMoney(SysUserMoney sysUserMoney, BigDecimal money, Boolean transterType, String remark, SysUser sysUse);

    /**
     * 异步推送消息到前端
     */
    void syncPushMoneyToWebApp(Long userId,String userName);

    SysUserMoney receiveWashCode(SysUserMoney userMoney);
}

