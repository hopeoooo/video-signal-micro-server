package com.central.user.consumer;

import com.central.common.model.CodeEnum;
import com.central.common.model.Result;
import com.central.common.model.SysUserAudit;
import com.central.common.model.SysUserAuditDetail;
import com.central.config.dto.BetMultipleDto;
import com.central.config.feign.ConfigService;
import com.central.game.model.GameRecord;
import com.central.user.feign.UserService;
import com.central.user.model.vo.SysUserMoneyVo;
import com.central.user.service.ISysUserAuditDetailService;
import com.central.user.service.ISysUserAuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/**
 * 打码消费者
 */
@Configuration
@Slf4j
public class FlowCodeConsumer {

    @Autowired
    private UserService userService;
    @Autowired
    private ISysUserAuditService sysUserAuditService;
    @Autowired
    private ISysUserAuditDetailService sysUserAuditDetailService;
    @Autowired
    private ConfigService configService;

    @Bean
    public Function<Flux<Message<GameRecord>>, Mono<Void>> flowCode() {
        return flux -> flux.map(message -> {
            GameRecord record = message.getPayload();
            log.info("接收到打码消息record={}", record.toString());
            calculateFlowCode(record);
            return message;
        }).then();
    }


    public void calculateFlowCode(GameRecord record) {
        if (record == null) {
            log.error("洗码record为空");
            return;
        }
        //查询未完成流水
        Result<SysUserMoneyVo> moneyResult = userService.getMoneyByUserName(record.getUserName());
        if (moneyResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            log.error("打码查询用户钱包失败，moneyResult={}", moneyResult);
            return;
        }
        BigDecimal money = moneyResult.getDatas().getMoney();
        BigDecimal unfinishedCode = moneyResult.getDatas().getUnfinishedCode();
        if (unfinishedCode.compareTo(BigDecimal.ZERO) < 1) {
            log.info("用户无未完成流水，无需打码,record={}", record);
            return;
        }
        BigDecimal validbet = record.getValidbet();
        //有效投注额大于0才计算
        if (ObjectUtils.isEmpty(validbet) || validbet.compareTo(BigDecimal.ZERO) < 1) {
            log.info("打码有效投注额为空，或小于0不计算,record={}", record.toString());
            return;
        }
        //判断是否已经处理过，防止重复计算
        Integer count = sysUserAuditDetailService.lambdaQuery().eq(SysUserAuditDetail::getGameRecordId, record.getId()).count();
        if (count > 0) {
            return;
        }
        //查询未完成的稽核记录
        SysUserAudit sysUserAudit = sysUserAuditService.lambdaQuery().eq(SysUserAudit::getOrderStatus, 1)
                .eq(SysUserAudit::getUserId, record.getUserId()).orderByDesc(SysUserAudit::getCreateTime).last("limit 1").one();
        if (sysUserAudit == null) {
            log.info("userId={},userName={}没有未完成的稽核记录", record.getUserId(), record.getUserName());
            return;
        }
        Result<BetMultipleDto> betMultipleResult = configService.findBetMultiple();
        if (betMultipleResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            log.error("打码清零点查询失败");
            return;
        }
        BigDecimal betZrrorPint = betMultipleResult.getDatas().getBetZrrorPint();
        if (ObjectUtils.isEmpty(betZrrorPint)) {
            betZrrorPint = BigDecimal.ZERO;
        }
        //扣减打码量
        subFlowCode(sysUserAudit, record.getUserId(), record.getUserName(), record.getId(), record.getBetId(), record.getValidbet(), betZrrorPint, money, unfinishedCode);
        log.info("打码完成，record={}", record.toString());
        //扣减userMoney未完成流水
        Result result = userService.updateUnfinishedCode(record.getUserId(), validbet);
        if (result.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            log.error("未完成流水userMoney更新失败,result={},record={}", result, record);
        } else {
            log.info("未完成流水userMoney更新成功,record={}", record.toString());
        }
    }

    /**
     * 扣减
     *
     * @param audit
     */
    public void subFlowCode(SysUserAudit audit, Long userId, String userName, Long gameRecordId, String betId, BigDecimal validbet, BigDecimal betZrrorPint, BigDecimal money, BigDecimal unfinishedCode) {
        if (ObjectUtils.isEmpty(audit.getUndoneValidBet()) || ObjectUtils.isEmpty(audit.getDoneValidBet()) || ObjectUtils.isEmpty(audit.getResidueValidBet())) {
            log.error("打码字段为空,audit={}", audit);
            return;
        }
        SysUserAuditDetail detail = new SysUserAuditDetail();
        detail.setUserId(userId);
        detail.setUserName(userName);
        detail.setGameRecordId(gameRecordId);
        detail.setBetId(betId);
        detail.setAuditId(audit.getId());
        //剩余打码大于有效投注
        if (audit.getResidueValidBet().compareTo(validbet) == 1) {
            audit.setDoneValidBet(audit.getDoneValidBet().add(validbet));
            audit.setResidueValidBet(audit.getUndoneValidBet().subtract(audit.getDoneValidBet()));
            //小于等于请零点
            if (audit.getResidueValidBet().compareTo(betZrrorPint) < 1) {
                audit.setResidueValidBet(BigDecimal.ZERO);
                audit.setDoneValidBet(audit.getUndoneValidBet());
                detail.setBetZrrorPint(betZrrorPint);
                //更新稽核记录表
                audit.setOrderStatus(2);
                audit.setUserAmount(money);
                //计算可提现金额
                BigDecimal withdrawAmount = getWithdrawAmount(userId, money, audit.getId());
                audit.setWithdrawAmount(withdrawAmount);
            }
            detail.setAmount(validbet);
            detail.setAmountBefore(unfinishedCode);
            detail.setAmountAfter(detail.getAmountBefore().subtract(detail.getAmount()));
            sysUserAuditService.updateById(audit);
            sysUserAuditDetailService.save(detail);
        } else {
            BigDecimal residueValidBet = audit.getResidueValidBet();
            audit.setResidueValidBet(BigDecimal.ZERO);
            audit.setDoneValidBet(audit.getUndoneValidBet());
            audit.setUserAmount(money);
            //计算可提现金额
            BigDecimal withdrawAmount = getWithdrawAmount(userId, money, audit.getId());
            audit.setWithdrawAmount(withdrawAmount);
            //更新稽核记录表
            audit.setOrderStatus(2);
            sysUserAuditService.updateById(audit);

            detail.setAmount(residueValidBet);
            detail.setAmountBefore(unfinishedCode);
            detail.setAmountAfter(detail.getAmountBefore().subtract(detail.getAmount()));
            //记录明细
            sysUserAuditDetailService.save(detail);
            //剩余有效打码
            BigDecimal surplusResidueValidBet = validbet.subtract(residueValidBet);
            //继续查询未完成稽核记录
            SysUserAudit sysUserAudit = sysUserAuditService.lambdaQuery().eq(SysUserAudit::getOrderStatus, 1)
                    .eq(SysUserAudit::getUserId, userId).orderByDesc(SysUserAudit::getCreateTime).last("limit 1").one();
            subFlowCode(sysUserAudit, userId, userName, gameRecordId, betId, surplusResidueValidBet, betZrrorPint, money, detail.getAmountAfter());
        }
    }

    public BigDecimal getWithdrawAmount(Long userId, BigDecimal money, Long auditId) {
        //计算可提现金额
        List<SysUserAudit> sysUserAuditList = sysUserAuditService.lambdaQuery().eq(SysUserAudit::getUserId, userId).eq(SysUserAudit::getOrderStatus, 1).list();
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
}