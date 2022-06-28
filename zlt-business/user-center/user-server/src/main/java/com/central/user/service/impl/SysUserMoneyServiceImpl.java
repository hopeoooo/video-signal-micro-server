package com.central.user.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.*;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.common.utils.BigDecimalUtils;
import com.central.config.dto.BetMultipleDto;
import com.central.config.feign.ConfigService;
import com.central.game.feign.GameService;
import com.central.game.model.vo.GameRoomGroupUserVo;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import com.central.user.mapper.SysUserMoneyMapper;
import com.central.user.model.vo.RankingListVo;
import com.central.user.service.ISysTansterMoneyLogService;
import com.central.user.service.ISysUserAuditService;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    @Autowired

    private GameService gameService;

    @Autowired
    @Lazy
    private ISysUserAuditService iSysUserAuditService;


    @Resource
    private ConfigService configService;

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
    @CachePut(key="#sysUserMoney.userId")
    public SysUserMoney updateCache(SysUserMoney sysUserMoney) {
        baseMapper.updateById(sysUserMoney);
        //金额设置的时候实体默认是null,缓存保存null,后面计算的时候会NPE,要查询一次缓存中保存0，后面计算的时候才不会有问题
        return baseMapper.selectById(sysUserMoney.getId());
    }

    /**
     * 更新金额和打码量
     *
     * @param sysUserMoney
     * @param money
     * @param transterType
     * @param remark
     * @param traceId
     * @param sysUser
     * @param betId
     * @param auditMultiple
     * @return
     */
    @Override
    @Transactional
    @CachePut(key="#sysUserMoney.userId")
    public SysUserMoney transterMoney(SysUserMoney sysUserMoney, BigDecimal money, Integer transterType,
                                      String remark, String traceId, SysUser sysUser, String betId, BigDecimal auditMultiple) {
        //查询游客打码量
        BetMultipleDto betMultipleDto = configService.findBetMultiple().getDatas();
        if(auditMultiple == null){ //默认一倍
            auditMultiple = betMultipleDto == null? BigDecimal.ONE : betMultipleDto.getBetMultiple();
        }

        BigDecimal userMoery = sysUserMoney.getMoney() == null ? BigDecimal.ZERO : sysUserMoney.getMoney();
        BigDecimal unFinishCode = sysUserMoney.getUnfinishedCode() == null ? BigDecimal.ZERO : sysUserMoney.getUnfinishedCode();
        CapitalEnum capitalEnum = CapitalEnum.fingCapitalEnumType(transterType);
        if (capitalEnum.getAddOrSub() == 0) {//上分
            sysUserMoney.setMoney(sysUserMoney.getMoney().add(money));
        } else if (capitalEnum.getAddOrSub() == 1) {
            //扣减金额大于本地余额时，最多只能扣减剩余的
            money = money.compareTo(sysUserMoney.getMoney()) == 1 ? sysUserMoney.getMoney() : money;
            sysUserMoney.setMoney(sysUserMoney.getMoney().subtract(money));
            money = money.negate();
        }

        if(transterType == CapitalEnum.ARTIFICIALIN.getType() || transterType == CapitalEnum.ARTIFICIALOUT.getType()){
            //设置洗码量
            setWashAmount(sysUserMoney, money, auditMultiple, transterType);
            //设置洗码量
            iSysUserAuditService.saveAuditDate(sysUserMoney, money, auditMultiple, transterType, sysUserMoney, betMultipleDto, unFinishCode);
        }
        baseMapper.updateById(sysUserMoney);

        //游客不记录数据
        if (!UserType.APP_GUEST.name().equals(sysUser.getType())) {
            SysTansterMoneyLog sysTansterMoneyLog = getSysTansterMoneyLog(userMoery, money, sysUser, remark, traceId, transterType, sysUserMoney.getMoney(), betId);
            iSysTansterMoneyLogService.save(sysTansterMoneyLog);
        }
        return sysUserMoney;
    }

    /**
     * 洗码量加扣减
     *
     * @param sysUserMoney
     * @param money
     * @param auditMultiple
     * @param transterType
     */
    private void setWashAmount(SysUserMoney sysUserMoney, BigDecimal money, BigDecimal auditMultiple, Integer transterType) {
        BigDecimal unFinishCode = sysUserMoney.getUnfinishedCode() == null ? BigDecimal.ZERO : sysUserMoney.getUnfinishedCode();
        if(transterType == CapitalEnum.ARTIFICIALIN.getType()){ //人工上分
            unFinishCode = unFinishCode.add(money.multiply(auditMultiple));
        }else{//人工下分
            unFinishCode = unFinishCode.subtract(money.multiply(auditMultiple));
            unFinishCode = unFinishCode.compareTo(BigDecimal.ZERO) <= 0? BigDecimal.ZERO : unFinishCode;
        }
        sysUserMoney.setUnfinishedCode(unFinishCode);
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
        vo = BigDecimalUtils.keepDecimal(vo);
        PushResult<SysUserMoneyVo> pushResult = PushResult.succeed(vo, SocketTypeConstant.MONEY,"用户钱包推送成功");
        Result<String> push = pushService.sendOneMessage(userName,JSONObject.toJSONString(pushResult));
        log.info("用户钱包userName:{},推送结果:{}", userId, push);
    }

    @Override
    @Async
    public void syncPushMoneyToTableNum(Long userId, String userName) {
        Result<List<GameRoomGroupUserVo>> listResult = gameService.getAllGroupListByUserName(userName);
        if (listResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            log.error("推送桌台分组用户金额时查询getAllGroupListByUserName失败");
            return;
        }
        LambdaQueryWrapper<SysUserMoney> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysUserMoney::getUserId, userId);
        SysUserMoney sysUserMoney = baseMapper.selectOne(lqw);
        List<GameRoomGroupUserVo> datas = listResult.getDatas();
        for (GameRoomGroupUserVo vo : datas) {
            vo.setGroupId(vo.getGroupId());
            vo.setStatus(1);
            vo.setUserName(userName);
            vo.setMoney(sysUserMoney.getMoney());
            vo.setGameId(vo.getGameId());
            vo.setTableNum(vo.getTableNum());
            String groupId = vo.getGameId() + "-" + vo.getTableNum();
            vo = BigDecimalUtils.keepDecimal(vo);
            PushResult<GameRoomGroupUserVo> pushResult = PushResult.succeed(vo, SocketTypeConstant.TABLE_GROUP_USER, "桌台分组用户信息推送成功");
            //推送下注界面
            Result<String> push = pushService.sendMessageByGroupId(groupId, com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
            log.info("下注界面桌台用户余额信息推送推送结果:groupId={},result={}", groupId, push);
        }
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

    @Override
    public List<RankingListVo> getRichList() {
        List<RankingListVo> richList = baseMapper.getRichList();
        for (RankingListVo vo : richList) {
            String userName = vo.getUserName();
            String benStr = userName.substring(0, 2);
            String endStr = userName.substring(userName.length() - 2);
            vo.setUserName(benStr + "***" + endStr);
        }
        return richList;
    }

    private SysTansterMoneyLog getSysTansterMoneyLog(BigDecimal beforeMoery, BigDecimal money, SysUser sysUser,
                                                     String remark, String traceId, Integer transterType, BigDecimal afterMoney,String betId) {
        SysTansterMoneyLog sysTansterMoneyLog = new SysTansterMoneyLog();
        sysTansterMoneyLog.setUserId(sysUser.getId());
        sysTansterMoneyLog.setUserName(sysUser.getUsername());
        sysTansterMoneyLog.setParent(sysUser.getParent());
        sysTansterMoneyLog.setMoney(money);
        sysTansterMoneyLog.setBeforeMoney(beforeMoery);
        sysTansterMoneyLog.setAfterMoney(afterMoney);
        sysTansterMoneyLog.setOrderType(transterType);
        if(StringUtils.isNotBlank(traceId)){
            sysTansterMoneyLog.setTraceId(traceId);
        }
        if(StringUtils.isNotBlank(remark)){
            sysTansterMoneyLog.setRemark(remark);
        }
        if(StringUtils.isNotBlank(betId)){
            sysTansterMoneyLog.setBetId(betId);
        }
        sysTansterMoneyLog.setOrderNo("SXF" + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS"));
        return sysTansterMoneyLog;
    }


}
