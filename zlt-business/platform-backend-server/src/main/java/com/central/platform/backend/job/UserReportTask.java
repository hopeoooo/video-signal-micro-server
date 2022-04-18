package com.central.platform.backend.job;

import com.central.common.model.Result;
import com.central.game.dto.UserReportDto;
import com.central.game.feign.GameService;
import com.central.platform.backend.model.UserReport;
import com.central.platform.backend.service.IUserReportService;
import com.central.user.dto.UserTansterMoneyDto;
import com.central.user.feign.TansterMoneyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class UserReportTask {

    @Autowired
    private IUserReportService iUserReportService;

    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH");

    private static final String start = ":00:00";
    private static final String end = ":59:59";
    private static final Integer charge = 5;
    private static final Integer withdraw = 6;

    @Resource
    private GameService gameService;

    @Resource
    private TansterMoneyService tansterMoneyService;

    @Scheduled(cron = "0 10 * * * ?")
    public void begin(){
        log.info("每小时统计会员报表开始");
        Calendar calendar = Calendar.getInstance();
        //设置小时
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        String startTime = df.format(calendar.getTime())+start;
        String endTime = df.format(calendar.getTime())+end;
        String today = startTime.substring(0,10);
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        Map<Long,UserReport> userReportMap = new HashMap<>();
        try {
            Result<List<UserReportDto>> userReportDto = gameService.findUserReportDto(map);
            List<UserReportDto> datas = userReportDto.getDatas();
            if (datas != null && !datas.isEmpty()){
                datas.forEach(userReportDto1 -> {
                    UserReport userReport = getUserReport(today);
                    BeanUtils.copyProperties(userReportDto1,userReport);
                    userReportMap.put(userReport.getUserId(),userReport);
                });
            }
            map.put("orderType",charge);
            List<UserTansterMoneyDto> charges = tansterMoneyService.findUserTansterMoneyDto(map).getDatas();
            if (charges != null && !charges.isEmpty()){
                charges.forEach(userTansterMoneyDto -> {
                    UserReport userReport = userReportMap.get(userTansterMoneyDto.getUserId());
                    if (userReport == null){
                        userReport = getUserReport(today);
                        userReport.setUserId(userTansterMoneyDto.getUserId());
                    }
                    userReport.setChargeAmount(userTansterMoneyDto.getMoney());
                    userReportMap.put(userTansterMoneyDto.getUserId(),userReport);
                });
            }
            map.put("orderType",withdraw);
            List<UserTansterMoneyDto> withdraws = tansterMoneyService.findUserTansterMoneyDto(map).getDatas();
            if (withdraws != null && !withdraws.isEmpty()){
                withdraws.forEach(userTansterMoneyDto -> {
                    UserReport userReport = userReportMap.get(userTansterMoneyDto.getUserId());
                    if (userReport == null){
                        userReport = getUserReport(today);
                        userReport.setUserId(userTansterMoneyDto.getUserId());
                    }
                    userReport.setWithdrawMoney(userTansterMoneyDto.getMoney());
                    userReportMap.put(userTansterMoneyDto.getUserId(),userReport);
                });
            }
            if (!userReportMap.isEmpty()){
                userReportMap.forEach((k,v)->{
                    v.setId(toHash(v.getUserId().toString()+today));
                    iUserReportService.saveUserReport(v);
                });
            }
        }catch (Exception ex){
            log.error("{}每小时会员报表出错{}",today,ex);
        }
    }

    private UserReport getUserReport(String today){
        UserReport userReport = new UserReport();
        userReport.setStaticsTimes(today);
        userReport.setChargeAmount(BigDecimal.ZERO);
        userReport.setValidAmount(BigDecimal.ZERO);
        userReport.setWinLoss(BigDecimal.ZERO);
        userReport.setWithdrawMoney(BigDecimal.ZERO);
        return userReport;
    }

    public static Long toHash(String key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(key.getBytes("utf-8"));
            BigInteger bigInt = new BigInteger(1, md5.digest());
            return Math.abs(bigInt.longValue());
        } catch (Exception ex) {
            return null;
        }

    }
}
