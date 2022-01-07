package com.central.oauth.service.impl;

import cn.hutool.core.date.DateTime;
import com.central.common.feign.UserService;
import com.central.common.model.LoginLog;
import com.central.common.model.SysUser;
import com.central.oauth.service.ProcessLoginInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class ProcessLoginInfoServiceImpl implements ProcessLoginInfoService {

    @Resource
    private UserService userService;

    @Async
    @Override
    public void processLoginInfo(UserDetails userDetails,String loginIp) {
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


}
