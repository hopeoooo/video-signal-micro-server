package com.central.platform.backend.job;

import com.central.user.feign.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class OnlineUserReportJob implements SimpleJob {

    @Autowired
    private OnlineUserService onlineUserService;

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("OnlineUserReportJobItem: {} | Time: {} | Thread: {} | {}",
            shardingContext.getShardingItem(), new SimpleDateFormat("HH:mm:ss").format(new Date()), Thread.currentThread().getId(), "SIMPLE");
        try {
            onlineUserService.onlineUserReport();
        }catch (Exception ex){
            log.error("在线人数统计失败{}", ex);
        }

    }
}
