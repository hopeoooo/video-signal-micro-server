package com.central.user.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.*;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import com.central.user.mapper.SysUserMoneyMapper;
import com.central.user.service.ISysTansterMoneyLogService;
import com.central.user.service.ISysUserMoneyService;
import com.central.user.model.vo.SysUserMoneyVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


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
        return PageResult.<SysUserMoney>builder().data(list).count(page.getTotal()).build();
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
        //金额设置的时候实体默认是null,缓存保存null,后面计算的时候会NPE,要查询一次缓存中保存0，后面计算的时候才不会有问题
        return baseMapper.selectById(sysUserMoney.getId());
    }

    @Override
    @Transactional
    @CachePut(key="#sysUserMoney.userId")
    public SysUserMoney transterMoney(SysUserMoney sysUserMoney, BigDecimal money, Integer transterType, String remark, String traceId, SysUser sysUser) {
        BigDecimal userMoery = sysUserMoney.getMoney()==null?BigDecimal.ZERO:sysUserMoney.getMoney();
        if (transterType == 1 || transterType == 2) {//上分
            sysUserMoney.setMoney(sysUserMoney.getMoney().add(money));
        } else if (transterType == 0 || transterType == 3) {
            //扣减金额大于本地余额时，最多只能扣减剩余的
            money = money.compareTo(sysUserMoney.getMoney()) == 1 ? sysUserMoney.getMoney() : money;
            sysUserMoney.setMoney(sysUserMoney.getMoney().subtract(money));
        }
        baseMapper.updateById(sysUserMoney);
        SysTansterMoneyLog sysTansterMoneyLog = getSysTansterMoneyLog(userMoery, money, sysUser, remark, traceId, transterType, sysUserMoney.getMoney());
        iSysTansterMoneyLogService.save(sysTansterMoneyLog);
        return sysUserMoney;
    }

    @Override
    @Async
    public void syncPushMoneyToWebApp(Long userId,String userName) {
        SysUserMoney money = findByUserId(userId);
        if (money == null) {
            money = new SysUserMoney();
        }
        SysUserMoneyVo vo = new SysUserMoneyVo();
        BeanUtils.copyProperties(money, vo);
        PushResult<SysUserMoneyVo> pushResult = PushResult.succeed(vo, SocketTypeConstant.MONEY,"用户钱包推送成功");
        Result<String> push = pushService.sendOneMessage(userName,JSONObject.toJSONString(pushResult));
        log.info("用户钱包userName:{},推送结果:{}", userId, push);
    }

    @Override
    @CachePut(key="#p0.userId")
    public SysUserMoney receiveWashCode(SysUserMoney userMoney) {
        userMoney.setMoney(userMoney.getMoney().add(userMoney.getWashCode()));
        userMoney.setWashCode(BigDecimal.ZERO);
        baseMapper.updateById(userMoney);
        return userMoney;
    }

    @Override
    public BigDecimal getSumMoneyByParent(String parent) {
        return baseMapper.getSumMoneyByParent(parent);
    }

    private SysTansterMoneyLog getSysTansterMoneyLog(BigDecimal beforeMoery, BigDecimal money, SysUser sysUser,
                                                     String remark, String traceId, Integer transterType, BigDecimal afterMoney) {
        SysTansterMoneyLog sysTansterMoneyLog = new SysTansterMoneyLog();
        sysTansterMoneyLog.setUserId(sysUser.getId());
        sysTansterMoneyLog.setUserName(sysUser.getUsername());
        sysTansterMoneyLog.setParent(sysUser.getParent());
        sysTansterMoneyLog.setMoney(money);
        sysTansterMoneyLog.setBeforeMoney(beforeMoery);
        sysTansterMoneyLog.setAfterMoney(afterMoney);
        if (transterType == 0) {
            sysTansterMoneyLog.setOrderType(CapitalEnum.ARTIFICIALOUT.getType());
        } else if (transterType == 1) {
            sysTansterMoneyLog.setOrderType(CapitalEnum.ARTIFICIALIN.getType());
        } else if (transterType == 2) {
            sysTansterMoneyLog.setOrderType(CapitalEnum.BUSINESS_ADD.getType());
        } else if (transterType == 3) {
            sysTansterMoneyLog.setOrderType(CapitalEnum.BUSINESS_SUB.getType());
        }
        if(StringUtils.isNotBlank(traceId)){
            sysTansterMoneyLog.setTraceId(traceId);
        }
        if(StringUtils.isNotBlank(remark)){
            sysTansterMoneyLog.setRemark(remark);
        }
        sysTansterMoneyLog.setOrderNo("SXF" + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS"));
        return sysTansterMoneyLog;
    }


}
