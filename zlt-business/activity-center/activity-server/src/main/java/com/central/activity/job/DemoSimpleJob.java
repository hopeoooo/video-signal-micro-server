package com.central.activity.job;

import com.central.activity.model.Foo;
import com.central.activity.model.FooRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 普通Job
 * DemoSimpleJob 为 通过spring配置实现自动启动定时任务
 */
@Slf4j
@Component
public class DemoSimpleJob implements SimpleJob {

    @Autowired
    private FooRepository fooRepository;

    @Override
    public void execute(final ShardingContext shardingContext) {
        log.info("Item: {} | Time: {} | Thread: {} | {}",
                shardingContext.getShardingItem(), new SimpleDateFormat("HH:mm:ss").format(new Date()), Thread.currentThread().getId(), "SIMPLE");
        List<Foo> data = fooRepository.findTodoData(shardingContext.getShardingParameter(), 10);
        for (Foo each : data) {
            fooRepository.setCompleted(each.getId());
        }
    }
}
