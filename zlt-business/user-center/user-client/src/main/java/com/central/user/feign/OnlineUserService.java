package com.central.user.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.OnlineUser;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.params.user.OnlineUserParams;
import com.central.user.feign.callback.OnlineUserServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 注释
 *
 * @author lance
 * @since 2022 -02-10 11:59:34
 */
@FeignClient(name = ServiceNameConstants.USER_SERVICE,fallbackFactory = OnlineUserServiceFallbackFactory.class, decode404 = true)
public interface OnlineUserService {

    /**
     * 查询在线人数
     *
     * @param params 入参释义
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -02-10 11:59:34
     */
    @GetMapping("/online/user/list")
    Result<List<OnlineUser>> list(@SpringQueryMap OnlineUserParams params);

    /**
     * 会员报表查询
     *
     * @param params 入参释义
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -02-10 11:59:34
     */
    @GetMapping("/online/user/findPageList")
    Result<PageResult<OnlineUser>> findPageList(@SpringQueryMap OnlineUserParams params);

    /**
     * 及时在线会员
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -02-10 11:59:34
     */
    @GetMapping("/online/user/queryPlayerNums")
    Result<Integer> queryPlayerNums();


    /**
     * 在线用户报告
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -02-10 12:15:38
     */
    @GetMapping("/online/user/onlineUserReport")
    Result<String> onlineUserReport();
}
