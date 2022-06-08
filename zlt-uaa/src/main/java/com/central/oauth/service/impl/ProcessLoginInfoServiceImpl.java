package com.central.oauth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.central.common.model.CodeEnum;
import com.central.common.model.LoginLog;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.vo.SysMoneyVO;
import com.central.config.dto.TouristDto;
import com.central.config.feign.ConfigService;
import com.central.game.feign.GameService;
import com.central.oauth.service.ProcessLoginInfoService;
import com.central.user.feign.UserService;
import com.central.user.model.co.SysUserCo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Service
public class ProcessLoginInfoServiceImpl implements ProcessLoginInfoService {

    @Autowired
    private UserService userService;
    @Autowired
    private ConfigService configService;
    @Resource
    private GameService gameService;

    @Async
    @Override
    public void processLoginInfo(UserDetails userDetails,String loginIp) {
        log.info("+++++++ logInIp is {}",loginIp);
        SysUser user = (SysUser) userDetails;
        SysUserCo sysUser = new SysUserCo();
        BeanUtil.copyProperties(user, sysUser);
        log.info("++++++++++++ sysUser {}",sysUser);
        sysUser.setLoginIp(loginIp);
        sysUser.setLogin(Boolean.TRUE);
        userService.saveOrUpdate(sysUser);
        addLoginLog(user,loginIp);
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
    public void initGuest(SysUser userDetails) {
        Result<TouristDto> touristDtoResult = configService.findTouristAmount();
        log.info("init Amount is {}",touristDtoResult);
        log.info("authentication is {}",userDetails);
        if (touristDtoResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            log.error("查询游客初始化余额失败");
            return;
        }
        TouristDto resultDatas = touristDtoResult.getDatas();
        if (resultDatas == null || ObjectUtils.isEmpty(resultDatas.getTouristAmount())) {
            log.error("查询游客初始化余额为空");
            return;
        }
        SysUser sysUser =  userDetails;
        BigDecimal maxAmount = touristDtoResult.getDatas().getTouristAmount();
        // 游客初始化金额  持久化
        SysMoneyVO sysMoneyVO = new SysMoneyVO();
        sysMoneyVO.setUserMoney(maxAmount);
        sysMoneyVO.setUid(sysUser.getId());
        userService.updateMoney(sysMoneyVO);
        //还原游客配置
        SysUser user = new SysUser();
        user.setId(sysUser.getId());
        user.setIsAutoBet(false);
        userService.updateSysUser(user);
    }

    @Async
    @Override
    public void clearGuestGameRecord(SysUser sysUser){
        gameService.clearGuestGameRecord(sysUser.getId());
    }

}
