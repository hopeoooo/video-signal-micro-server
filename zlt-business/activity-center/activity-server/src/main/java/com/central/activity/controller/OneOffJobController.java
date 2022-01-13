//package com.central.activity.controller;
//
//import io.swagger.annotations.Api;
//import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Api(tags = "活动模块api")
//@RestController
//public class OneOffJobController {
//
//    private static final String RES_TEXT = "{\"msg\":\"OK\"}";
//
//    @Autowired
//    @Qualifier(value = "occurErrorNoticeDingtalkBean")
//    private OneOffJobBootstrap occurErrorNoticeDingtalkJob;
//
//    @Autowired
//    @Qualifier(value = "occurErrorNoticeWechatBean")
//    private OneOffJobBootstrap occurErrorNoticeWechatJob;
//
//    @Autowired
//    @Qualifier(value = "occurErrorNoticeEmailBean")
//    private OneOffJobBootstrap occurErrorNoticeEmailJob;
//
//    @GetMapping("/execute/occurErrorNoticeDingtalkJob")
//    public String executeOneOffJob() {
//        occurErrorNoticeDingtalkJob.execute();
//        return RES_TEXT;
//    }
//
//    @GetMapping("/execute/occurErrorNoticeWechatJob")
//    public String executeOccurErrorNoticeWechatJob() {
//        occurErrorNoticeWechatJob.execute();
//        return RES_TEXT;
//    }
//
//    @GetMapping("/execute/occurErrorNoticeEmailJob")
//    public String executeOccurErrorNoticeEmailJob() {
//        occurErrorNoticeEmailJob.execute();
//        return RES_TEXT;
//    }
//}
