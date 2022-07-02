package com.central.user.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.*;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.config.dto.BetMultipleDto;
import com.central.user.mapper.SysUserAuditMapper;
import com.central.user.model.co.AddUserAuditCo;
import com.central.user.model.co.SysUserAuditPageCo;
import com.central.user.model.vo.SysUserAuditVo;
import com.central.user.service.ISysUserAuditDetailService;
import com.central.user.service.ISysUserAuditService;
import com.central.user.service.ISysUserMoneyService;
import com.central.user.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Constants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SysUserAuditServiceImpl extends SuperServiceImpl<SysUserAuditMapper, SysUserAudit> implements ISysUserAuditService {

    @Autowired
    private SysUserAuditMapper sysUserAuditMapper;

    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private ISysUserAuditDetailService iSysUserAuditDetailService;

    @Autowired
    private ISysUserMoneyService userMoneyService;

    /**
     * 查询账变稽核记录
     *
     * @param params
     * @return
     */
    @Override
    public PageResult<SysUserAuditVo> findSysUserAuditList(SysUserAuditPageCo params) {
        Page<SysUserAudit> page = new Page<>(params.getPage(), params.getLimit());
        List<SysUserAudit> list = baseMapper.findList(page, params);

        List<SysUserAuditVo> sysUserAuditVoList = new ArrayList<>();
        for (SysUserAudit sysUserAudit : list) {
            SysUserAuditVo userAuditVo = new SysUserAuditVo();
            BeanUtils.copyProperties(sysUserAudit, userAuditVo);
            String orderTypeName = CapitalEnum.fingCapitalEnumType(sysUserAudit.getAuditType()).getName();
            userAuditVo.setOrderTypeName(orderTypeName);
            userAuditVo.setOrderType(sysUserAudit.getOrderStatus());
            sysUserAuditVoList.add(userAuditVo);
        }
        long total = page.getTotal();
        return PageResult.<SysUserAuditVo>builder().data(sysUserAuditVoList).count(total).build();
    }

    @Override
    public void saveAuditDate(SysUserMoney sysUserMoney, BigDecimal money, BigDecimal auditMultiple,
                              Integer transterType, SysUserMoney saveSysUserMoney, BetMultipleDto betMultipleDto, BigDecimal unFinishCode) {
        SysUser sysUser = iSysUserService.selectById(sysUserMoney.getUserId());
        if(transterType == CapitalEnum.ARTIFICIALIN.getType()){ //人工上分
            //保存稽核数据
            saveAudit(sysUserMoney, money, auditMultiple, sysUser);
        }else{
            BigDecimal zrrorPint = betMultipleDto == null ? BigDecimal.ZERO : betMultipleDto.getBetZrrorPint();
            //清零点判断。
            if(sysUserMoney.getMoney().compareTo(zrrorPint) <= 0){//达到清零点
                this.zrrorPointAudit(sysUserMoney, money, auditMultiple, sysUser, unFinishCode, zrrorPint);
            }else{
                SysUserAudit sysUserAudit = super.lambdaQuery().eq(SysUserAudit::getOrderStatus, 1)
                        .eq(SysUserAudit::getUserId, sysUserMoney.getUserId()).orderByDesc(SysUserAudit::getCreateTime).last("limit 1").one();
                if (sysUserAudit == null) {
                    log.info("userId={},userName={}没有未完成的稽核记录", sysUser.getId(), sysUser.getUsername());
                    return;
                }
                //减去对应打码量
                subFlowCode(sysUserAudit, sysUser, money, zrrorPint, sysUserMoney.getMoney(), unFinishCode);
            }
        }
    }

    @Transactional
    @Override
    public Result addAudit(AddUserAuditCo params) {

        SysUserAudit sysUserAudit = super.lambdaQuery().eq(SysUserAudit::getOrderStatus, 1)
                .eq(SysUserAudit::getUserName, params.getUserName())
                .eq(SysUserAudit::getOrderNo, params.getOrderNo())
                .orderByDesc(SysUserAudit::getCreateTime).last("limit 1").one();
        if(sysUserAudit == null){
            return Result.failed("订单不存在");
        }
        //更新打码量
        SysUserMoney sysUserMoney = userMoneyService.findByUserId(sysUserAudit.getUserId());
        BigDecimal unfinishedCode = sysUserMoney.getUnfinishedCode();
        sysUserMoney.setUnfinishedCode(sysUserMoney.getUnfinishedCode().add(params.getBetAmount()));
        userMoneyService.updateById(sysUserMoney);

        //更新稽核
        sysUserAudit.setUndoneValidBet(sysUserAudit.getUndoneValidBet().add(params.getBetAmount()));
        sysUserAudit.setResidueValidBet(sysUserAudit.getResidueValidBet().add(params.getBetAmount()));
        sysUserAudit.setUserAmount(sysUserMoney.getMoney());
        super.updateById(sysUserAudit);

        //记录打码量变化日志
        SysUserAuditDetail userAuditDetail = new SysUserAuditDetail();
        userAuditDetail.setUserId(sysUserAudit.getUserId());
        userAuditDetail.setUserName(sysUserAudit.getUserName());
        userAuditDetail.setAuditId(sysUserAudit.getId());
        userAuditDetail.setBetId(sysUserAudit.getOrderNo());
        userAuditDetail.setAmount(params.getBetAmount());
        userAuditDetail.setAmountBefore(unfinishedCode);
        userAuditDetail.setAmountAfter(sysUserMoney.getUnfinishedCode());
        iSysUserAuditDetailService.save(userAuditDetail);
        return Result.succeed();
    }

    /**
     * 解锁打码量
     *
     * @param params
     * @return
     */
    @Override
    public Result subtractAudit(AddUserAuditCo params) {
        SysUserAudit sysUserAudit = super.lambdaQuery().eq(SysUserAudit::getOrderStatus, 1)
                .eq(SysUserAudit::getUserName, params.getUserName())
                .eq(SysUserAudit::getOrderNo, params.getOrderNo())
                .orderByDesc(SysUserAudit::getCreateTime).last("limit 1").one();

        if(sysUserAudit == null){
            return Result.failed("订单不存在");
        }

        SysUserMoney sysUserMoney = userMoneyService.findByUserId(sysUserAudit.getUserId());
        BigDecimal unfinishedCode = sysUserMoney.getUnfinishedCode();
        BigDecimal residueValidBet = sysUserAudit.getResidueValidBet();
        BigDecimal balance = (unfinishedCode.subtract(residueValidBet)).compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : unfinishedCode.subtract(residueValidBet);
        sysUserMoney.setUnfinishedCode(balance);
        userMoneyService.updateById(sysUserMoney);

        sysUserAudit.setOrderStatus(2);
        sysUserAudit.setResidueValidBet(BigDecimal.ZERO);
        List<SysUserAudit> sysUserAuditList = super.lambdaQuery().eq(SysUserAudit::getOrderStatus, 1)
                .eq(SysUserAudit::getUserName, params.getUserName())
                .notIn(SysUserAudit::getOrderNo, params.getOrderNo())
                .orderByDesc(SysUserAudit::getCreateTime).list();

        if(sysUserAuditList.isEmpty()){
            sysUserAudit.setWithdrawAmount(sysUserMoney.getMoney());
        }else{
            BigDecimal money = sysUserMoney.getMoney();
            for (SysUserAudit userAudit : sysUserAuditList) {
                money = money.subtract(userAudit.getAuditAmount());
                money = money.compareTo(BigDecimal.ZERO) <= 0? BigDecimal.ZERO : money;
                sysUserAudit.setWithdrawAmount(money);
            }
        }
        super.updateById(sysUserAudit);

        //记录打码量变化日志
        SysUserAuditDetail userAuditDetail = new SysUserAuditDetail();
        userAuditDetail.setUserId(sysUserAudit.getUserId());
        userAuditDetail.setUserName(sysUserAudit.getUserName());
        userAuditDetail.setAuditId(sysUserAudit.getId());
        userAuditDetail.setBetId(sysUserAudit.getOrderNo());
        userAuditDetail.setAmount(residueValidBet);
        userAuditDetail.setAmountBefore(unfinishedCode);
        userAuditDetail.setAmountAfter(BigDecimal.ZERO);
        iSysUserAuditDetailService.save(userAuditDetail);
        return Result.succeed();
    }

    public void subFlowCode(SysUserAudit audit, SysUser sysUser, BigDecimal money, BigDecimal betZrrorPint, BigDecimal userMoney, BigDecimal unfinishedCode) {
        if (ObjectUtils.isEmpty(audit.getUndoneValidBet()) || ObjectUtils.isEmpty(audit.getDoneValidBet()) || ObjectUtils.isEmpty(audit.getResidueValidBet())) {
            log.error("打码字段为空,audit={}", audit);
            return;
        }
        if(money.compareTo(BigDecimal.ZERO) < 1){
            money = money.negate();
        }
        SysUserAuditDetail detail = new SysUserAuditDetail();
        detail.setUserId(sysUser.getId());
        detail.setUserName(sysUser.getUsername());
        detail.setBetId(audit.getOrderNo());
        detail.setAuditId(audit.getId());
        //剩余打码大于有效投注
        if (audit.getResidueValidBet().compareTo(money.negate()) == 1) {
            audit.setDoneValidBet(audit.getDoneValidBet().add(money));
            audit.setResidueValidBet(audit.getUndoneValidBet().subtract(audit.getDoneValidBet()));
            //小于等于请零点
            if (audit.getResidueValidBet().compareTo(betZrrorPint) < 1) {
                audit.setResidueValidBet(BigDecimal.ZERO);
                audit.setDoneValidBet(audit.getUndoneValidBet());
                detail.setBetZrrorPint(betZrrorPint);
                //更新稽核记录表
                audit.setOrderStatus(2);
                audit.setUserAmount(userMoney);
                //计算可提现金额
                BigDecimal withdrawAmount = getWithdrawAmount(sysUser.getId(), userMoney, audit.getId());
                audit.setWithdrawAmount(withdrawAmount);
            }
            detail.setAmount(money);
            detail.setAmountBefore(unfinishedCode);
            detail.setAmountAfter(detail.getAmountBefore().subtract(detail.getAmount()));
            super.updateById(audit);
            iSysUserAuditDetailService.save(detail);
        } else {
            BigDecimal residueValidBet = audit.getResidueValidBet();
            audit.setResidueValidBet(BigDecimal.ZERO);
            audit.setDoneValidBet(audit.getUndoneValidBet());
            audit.setUserAmount(userMoney);
            //计算可提现金额
            BigDecimal withdrawAmount = getWithdrawAmount(sysUser.getId(), userMoney, audit.getId());
            audit.setWithdrawAmount(withdrawAmount);
            //更新稽核记录表
            audit.setOrderStatus(2);
            super.updateById(audit);

            detail.setAmount(residueValidBet);
            detail.setAmountBefore(unfinishedCode);
            detail.setAmountAfter(detail.getAmountBefore().subtract(detail.getAmount()));
            //记录明细
            iSysUserAuditDetailService.save(detail);
            //剩余有效打码
            BigDecimal surplusResidueValidBet = money.subtract(residueValidBet);
            //剩余打码等于0
            if (surplusResidueValidBet.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
            //继续查询未完成稽核记录
            SysUserAudit sysUserAudit = super.lambdaQuery().eq(SysUserAudit::getOrderStatus, 1)
                    .eq(SysUserAudit::getUserId, sysUser.getId()).orderByDesc(SysUserAudit::getCreateTime).last("limit 1").one();
            subFlowCode(sysUserAudit, sysUser, surplusResidueValidBet, betZrrorPint, userMoney, detail.getAmountAfter());
        }
    }

    public BigDecimal getWithdrawAmount(Long userId, BigDecimal money, Long auditId) {
        //计算可提现金额
        List<SysUserAudit> sysUserAuditList = super.lambdaQuery().eq(SysUserAudit::getUserId, userId).eq(SysUserAudit::getOrderStatus, 1).list();
        //假如只有一笔稽核，当他打码量为0时，可提现就是账号余额
        if (sysUserAuditList.size() == 1) {
            return money;
        }
        //假如这笔稽核完成的时候还有未完成的，这个时候这笔订单的可提现就是用账号余额-未完成稽核记录的总稽核金额，小于0就写0
        BigDecimal totalAuditAmount = sysUserAuditList.stream().filter(d -> d.getId() != auditId).map(SysUserAudit::getAuditAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal withdrawAmount = money.subtract(totalAuditAmount);
        withdrawAmount = withdrawAmount.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : withdrawAmount;
        return withdrawAmount;
    }


    /**
     * 打码量清零点
     *
     * @param sysUserMoney
     * @param money 下分金额
     * @param auditMultiple 打码倍数
     * @param sysUser
     * @param unFinishCode 未完成打码量
     * @param zrrorPint 清零点
     */
    private void zrrorPointAudit(SysUserMoney sysUserMoney, BigDecimal money, BigDecimal auditMultiple, SysUser sysUser, BigDecimal unFinishCode,BigDecimal zrrorPint) {
        //所有稽核列表数据清零
        List<SysUserAudit> sysUserAuditList = super.lambdaQuery().eq(SysUserAudit::getOrderStatus, 1)
                .eq(SysUserAudit::getUserId, sysUserMoney.getUserId()).orderByDesc(SysUserAudit::getCreateTime).list();
        for (SysUserAudit sysUserAudit : sysUserAuditList) {
            sysUserAudit.setOrderStatus(2);
            sysUserAudit.setResidueValidBet(BigDecimal.ZERO);
            sysUserAudit.setUserAmount(sysUserMoney.getMoney());
            sysUserAudit.setWithdrawAmount(BigDecimal.ZERO);
        }
        super.saveBatch(sysUserAuditList);
        //记录一条清零点日志
        SysUserAuditDetail userAuditDetail = new SysUserAuditDetail();
        userAuditDetail.setUserId(sysUser.getId());
        userAuditDetail.setUserName(sysUser.getUsername());
        userAuditDetail.setBetId("JH" + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS"));
        BigDecimal multiply = money.multiply(auditMultiple);
        userAuditDetail.setAmount(multiply);
        userAuditDetail.setAmountBefore(unFinishCode);
        userAuditDetail.setAmountAfter(BigDecimal.ZERO);
        userAuditDetail.setBetZrrorPint(zrrorPint);
        iSysUserAuditDetailService.save(userAuditDetail);
    }

    /**
     * 添加稽核列表数据
     *
     * @param sysUserMoney
     * @param money
     * @param auditMultiple
     */
    private void saveAudit(SysUserMoney sysUserMoney, BigDecimal money, BigDecimal auditMultiple,SysUser sysUser) {

        SysUserAudit sysUserAudit = new SysUserAudit();
        sysUserAudit.setUserId(sysUser.getId());
        sysUserAudit.setUserName(sysUser.getUsername());
        sysUserAudit.setOrderNo("JH" + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS"));
        sysUserAudit.setAuditType(CapitalEnum.ARTIFICIALIN.getType());
        sysUserAudit.setOrderStatus(Constants.ACC_PUBLIC);
        sysUserAudit.setAuditAmount(money);
        BigDecimal multiply = money.multiply(auditMultiple);
        sysUserAudit.setUndoneValidBet(multiply);
        sysUserAudit.setDoneValidBet(BigDecimal.ZERO);
        sysUserAudit.setResidueValidBet(multiply);
        sysUserAudit.setWithdrawAmount(BigDecimal.ZERO);
        sysUserAuditMapper.insert(sysUserAudit);

        SysUserAuditDetail userAuditDetail = new SysUserAuditDetail();
        userAuditDetail.setUserId(sysUser.getId());
        userAuditDetail.setUserName(sysUser.getUsername());
        userAuditDetail.setAuditId(sysUserAudit.getUserId());
        userAuditDetail.setBetId(sysUserAudit.getOrderNo());
        userAuditDetail.setAmount(multiply);
        BigDecimal unFinishCode = sysUserMoney.getUnfinishedCode().subtract(multiply);
        userAuditDetail.setAmountBefore(unFinishCode.compareTo(BigDecimal.ZERO) < 0? BigDecimal.ZERO : unFinishCode);
        userAuditDetail.setAmountAfter(sysUserMoney.getUnfinishedCode());
        iSysUserAuditDetailService.save(userAuditDetail);
    }
}
