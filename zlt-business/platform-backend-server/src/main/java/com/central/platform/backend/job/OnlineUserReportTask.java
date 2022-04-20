//package com.central.platform.backend.job;
//
//import com.central.user.feign.OnlineUserService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class OnlineUserReportTask {
//
//    @Autowired
//    private OnlineUserService onlineUserService;
//
//    @Scheduled(cron = "0 0/5 * * * ? ")
//    public void begin(){
//        onlineUserService.onlineUserReport();
//    }
//}
