package com.central.oauth.service;


import org.springframework.security.core.userdetails.UserDetails;

public interface ProcessLoginInfoService {

    void processLoginInfo(UserDetails userDetails,String loginIp);

}