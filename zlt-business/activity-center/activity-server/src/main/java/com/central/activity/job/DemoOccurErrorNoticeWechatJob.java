package com.central.activity.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

/**
 * 发生错误通知微信作业
 */
@Component
public class DemoOccurErrorNoticeWechatJob implements SimpleJob {
    
    @Override
    public void execute(final ShardingContext shardingContext) {
        throw new RuntimeException(String.format("An exception has occurred in Job, The parameter is %s", shardingContext.getShardingParameter()));
    }
}
