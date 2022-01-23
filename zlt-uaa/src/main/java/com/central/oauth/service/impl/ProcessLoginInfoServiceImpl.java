package com.central.oauth.service.impl;

import cn.hutool.core.date.DateTime;
import com.central.common.model.LoginLog;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.vo.SysMoneyVO;
import com.central.config.dto.TouristDto;
import com.central.config.feign.ConfigService;
import com.central.oauth.service.ProcessLoginInfoService;
import com.central.user.feign.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Service
public class ProcessLoginInfoServiceImpl implements ProcessLoginInfoService {

    @Resource
    private UserService userService;

    @Resource
    private ConfigService configService;

    @Async
    @Override
    public void processLoginInfo(UserDetails userDetails,String loginIp) {
        log.info("+++++++ logInIp is {}",loginIp);
        SysUser sysUser = (SysUser) userDetails;
        log.info("++++++++++++ sysUser {}",sysUser);
        sysUser.setLoginIp(loginIp);
        sysUser.setLogin(Boolean.TRUE);
        userService.saveOrUpdate(sysUser);
        addLoginLog(sysUser,loginIp);
    }

    private void addLoginLog(SysUser sysUser,String loginIp){
        LoginLog loginLog = new LoginLog();
        loginLog.setLoginIp(loginIp);
        loginLog.setLoginTime(DateTime.now());
        loginLog.setPlatName("webApp");
        loginLog.setUserId(sysUser.getId());
        userService.addLoginlog(loginLog);
    }

    @Async
    @Override
    public void initAmount(SysUser userDetails) {
        Result<TouristDto> touristDtoResult = configService.findTouristAmount();
        log.info("init Amount is {}",touristDtoResult);
        log.info("authentication is {}",userDetails);
        SysUser sysUser =  userDetails;
        BigDecimal maxAmount = touristDtoResult.getDatas().getTouristAmount();
        // 游客初始化金额  持久化
        SysMoneyVO sysMoneyVO = new SysMoneyVO();
        sysMoneyVO.setUserMoney(maxAmount);
        sysMoneyVO.setUid(sysUser.getId());
        userService.updateMoney(sysMoneyVO);
    }

}
