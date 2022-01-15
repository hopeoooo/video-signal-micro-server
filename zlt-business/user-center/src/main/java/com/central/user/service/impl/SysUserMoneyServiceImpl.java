package com.central.user.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.*;
import com.central.push.feign.PushService;
import com.central.user.mapper.SysUserMoneyMapper;
import com.central.user.service.ISysTansterMoneyLogService;
import com.central.user.service.ISysUserMoneyService;
import com.central.user.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.service.impl.SuperServiceImpl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;


/**
 * 用户钱包表
 *
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
@Slf4j
@Service
@CacheConfig(cacheNames = {"sysUserMoney"})
public class SysUserMoneyServiceImpl extends SuperServiceImpl<SysUserMoneyMapper, SysUserMoney> implements ISysUserMoneyService {

    @Autowired
    private ISysTansterMoneyLogService iSysTansterMoneyLogService;
    @Autowired
    private PushService pushService;


    /**
     * 列表
     * @param params
     * @return
     */
    @Override
    public PageResult<SysUserMoney> findList(Map<String, Object> params){
        Page<SysUserMoney> page = new Page<>(MapUtils.getInteger(params, "page"), MapUtils.getInteger(params, "limit"));
        List<SysUserMoney> list  =  baseMapper.findList(page, params);
        return PageResult.<SysUserMoney>builder().data(list).code(0).count(page.getTotal()).build();
    }

    @Override
    @Cacheable(key = "#userId")
    public SysUserMoney findByUserId(Long userId) {
        LambdaQueryWrapper<SysUserMoney> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysUserMoney::getUserId, userId);
        return baseMapper.selectOne(lqw);
    }

    @Override
    @CachePut(key="#sysUserMoney.userId")
    public SysUserMoney saveCache(SysUserMoney sysUserMoney) {
        baseMapper.insert(sysUserMoney);
        return sysUserMoney;
    }

    @Override
    @Transactional
    @CachePut(key="#sysUserMoney.userId")
    public SysUserMoney transterMoney(SysUserMoney sysUserMoney, BigDecimal money, Boolean transterType, String remark, SysUser sysUser) {
        BigDecimal userMoery = sysUserMoney.getMoney();
        if(transterType){//上分
            sysUserMoney.setMoney(sysUserMoney.getMoney().add(money));
        }else{
            sysUserMoney.setMoney(sysUserMoney.getMoney().subtract(money));
        }
        baseMapper.updateById(sysUserMoney);
        SysTansterMoneyLog sysTansterMoneyLog = getSysTansterMoneyLog(userMoery, money, sysUser, remark, transterType, sysUserMoney.getMoney());
        iSysTansterMoneyLogService.save(sysTansterMoneyLog);
        return sysUserMoney;
    }

    @Override
    @Async
    public void syncPushMoneyToWebApp(Long userId) {
        SysUserMoney money = findByUserId(userId);
        PushResult<SysUserMoney> pushResult = PushResult.succeed(money, "money");
        Result<String> push = pushService.pushOne(JSONObject.toJSONString(pushResult), userId.toString());
        log.info("用户金额userId:{},推送结果:{}", userId, push);
    }

    private SysTansterMoneyLog getSysTansterMoneyLog(BigDecimal beforeMoery, BigDecimal money, SysUser sysUser,
                                                     String remark, Boolean transterType, BigDecimal afterMoney) {
        SysTansterMoneyLog sysTansterMoneyLog = new SysTansterMoneyLog();
        sysTansterMoneyLog.setUserId(sysUser.getId());
        sysTansterMoneyLog.setUserName(sysUser.getUsername());
        sysTansterMoneyLog.setMoney(money);
        sysTansterMoneyLog.setBeforeMoney(beforeMoery);
        sysTansterMoneyLog.setAfterMoney(afterMoney);
        if(transterType){
            sysTansterMoneyLog.setOrderType(CapitalEnum.ARTIFICIALIN.getType());
        }else{
            sysTansterMoneyLog.setOrderType(CapitalEnum.ARTIFICIALOUT.getType());
        }
        if(StringUtils.isNotBlank(remark)){
            sysTansterMoneyLog.setRemark(remark);
        }
        sysTansterMoneyLog.setOrderNo("SXF" + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS"));
        return sysTansterMoneyLog;
    }


}
