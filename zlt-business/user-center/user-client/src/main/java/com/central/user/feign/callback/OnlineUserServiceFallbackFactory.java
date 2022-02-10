package com.central.user.feign.callback;

import com.central.common.model.OnlineUser;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.params.user.OnlineUserParams;
import com.central.user.feign.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class OnlineUserServiceFallbackFactory implements FallbackFactory<OnlineUserService> {

    @Override
    public OnlineUserService create(Throwable cause) {
        return new OnlineUserService() {
            @Override
            public Result<List<OnlineUser>> maps(@RequestParam("tag") Integer tag) {
                log.error("服务器异常, maps查询异常: {}", tag);
                return Result.succeed(Collections.emptyList());
            }

            @Override
            public Result<PageResult<OnlineUser>> findPageList(OnlineUserParams params) {
                log.error("服务器异常, findPageList查询异常: {}", params);
                PageResult<OnlineUser> page = new PageResult<>();
                page.setCount(0L);
                page.setData(Collections.emptyList());
                return Result.succeed(page);
            }

            @Override
            public Result<Integer> queryPlayerNums() {
                log.error("服务器异常, queryPlayerNums异常");
                return Result.succeed(0);
            }

            @Override
            public Result<String> onlineUserReport() {
                log.error("服务器异常, onlineUserReport异常");
                return Result.succeed();
            }
        };
    }
}
