package com.central.user.consumer;

import com.central.game.model.GameRecord;
import com.central.user.feign.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * 打码消费者
 */
@Configuration
@Slf4j
public class FlowCodeConsumer {

    @Autowired
    private UserService userService;

    @Bean
    public Function<Flux<Message<GameRecord>>, Mono<Void>> flowCode() {
        return flux -> flux.map(message -> {
            GameRecord record = message.getPayload();
            log.info("接收到打码消息record={}", record.toString());
//            calculateWashCode(record);
            return message;
        }).then();
    }

   /* public void calculateWashCode(GameRecord record) {
        if (record == null) {
            log.error("洗码record为空");
            return;
        }
        BigDecimal validbet = record.getValidbet();
        //有效投注额大于0才计算
        if (ObjectUtils.isEmpty(validbet) || validbet.compareTo(BigDecimal.ZERO) < 1) {
            log.info("洗码有效投注额为空，或小于0不计算,record={}", record.toString());
            return;
        }
        //判断是否已经处理过，防止重复计算
        Integer count = washCodeChangeService.lambdaQuery().eq(WashCodeChange::getGameRecordId, record.getId()).count();
        if (count > 0) {
            return;
        }
        //查询最新的洗码返水配置
        Result<List<UserWashCodeConfig>> codeConfigResult = userService.findWashCodeConfigListByGameIdAndUserId(Long.parseLong(record.getGameId()),record.getUserId());
        if (codeConfigResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            log.error("洗码配置查询失败,result={},record={}", codeConfigResult.toString(), record.toString());
            return;
        }
        List<UserWashCodeConfig> codeConfigList = codeConfigResult.getDatas();
        if (CollectionUtils.isEmpty(codeConfigList)) {
            log.error("洗码列表配置为空,record={}", record.toString());
            return;
        }
        UserWashCodeConfig washCodeConfig = codeConfigList.get(0);
        if (washCodeConfig == null) {
            log.error("洗码明细配置为空,record={}", record.toString());
            return;
        }
        BigDecimal gameRate = washCodeConfig.getGameRate();
        if (ObjectUtils.isEmpty(gameRate) || gameRate.compareTo(BigDecimal.ZERO) < 1) {
            log.error("洗码明细配置gameRate为空或0,washCodeConfig={},record={}", washCodeConfig.toString(), record.toString());
            return;
        }
        //转化百分比
        BigDecimal rate = gameRate.divide(new BigDecimal("100"));
        BigDecimal washCodeVal = validbet.multiply(rate);
        if (washCodeVal.compareTo(BigDecimal.ZERO) < 1) {
            log.error("洗码金额计算为0,washCodeVal={},gameRate={},record={}", washCodeVal, gameRate, record.toString());
            return;
        }
        WashCodeChange washCodeChange = new WashCodeChange();
        washCodeChange.setUserId(record.getUserId());
        washCodeChange.setUserName(record.getUserName());
        washCodeChange.setGameId(Long.parseLong(record.getGameId()));
        washCodeChange.setGameName(record.getGameName());
        washCodeChange.setRate(gameRate);
        washCodeChange.setValidbet(validbet);
        washCodeChange.setGameRecordId(record.getId());
        washCodeChange.setBetId(record.getBetId());
        washCodeChange.setAmount(washCodeVal);
        try {
            washCodeChangeService.save(washCodeChange);
            log.info("洗码明细记录成功,washCodeChange={},record={}", washCodeChange.toString(), record.toString());
            //把洗码额加回userMoney
            Result result = userService.updateWashCode(record.getUserId(), washCodeVal);
            if (result.getResp_code() != CodeEnum.SUCCESS.getCode()) {
                log.error("洗码金额加回userMoney失败,result={},record={},washCodeChange={}", result, record, washCodeChange);
            } else {
                log.info("洗码金额加回userMoney成功,washCodeChange={},record={}", washCodeChange.toString(), record.toString());
            }
        } catch (Exception e) {
            log.error("洗码失败，record={},msg={}", record.toString(), e.getMessage());
            e.printStackTrace();
        }
    }*/
}