package com.central.user.controller;

import com.central.common.constant.SecurityConstants;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.user.model.co.OnlineUserCo;
import com.central.common.redis.template.RedisRepository;
import com.central.common.model.OnlineUser;
import com.central.user.service.IOnlineUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 注释
 *
 * @author lance
 * @since 2022 -02-10 11:53:47
 */
@Slf4j
@RestController
@Api(tags = "在线会员报表api")
@RequestMapping("/online/user")
public class OnlineUserController {

    @Autowired
    private IOnlineUserService onlineUserService;

    @Autowired
    private RedisRepository redisRepository;

    /**
     * 在线会员报表走势图查询
     *
     * @param tag 入参释义
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -02-10 11:53:47
     */
    @ApiOperation(value = "在线会员报表走势图查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag", value = "tag:0 当日 tag:1 当月", required = true, dataType = "Integer"),
    })
    @GetMapping("/maps")
    public Result<List<OnlineUser>> maps(@RequestParam("tag") Integer tag) {
        return Result.succeed(onlineUserService.findOnlineUserMaps(tag));
    }

    /**
     * 会员报表查询
     *
     * @param params 入参释义
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -02-10 11:53:47
     */
    @ApiOperation(value = "会员报表查询")
    @GetMapping("/findPageList")
    public Result<PageResult<OnlineUser>> findPageList(@ModelAttribute OnlineUserCo params) {
        PageResult<OnlineUser> pageList = onlineUserService.findPageList(params);
        return Result.succeed(pageList);
    }

    /**
     * 及时在线会员
     */
    @ApiOperation(value = "及时在线会员")
    @GetMapping("/queryPlayerNums")
    public Result<Integer> queryPlayerNums() {
        String redisKey = SecurityConstants.REDIS_UNAME_TO_ACCESS+"online";
        Set<String> keySet = redisRepository.keys(redisKey+"*");
        return Result.succeed(keySet.size());
    }

    private static String patten = "yyyy-MM-dd HH:mm";

    public static SimpleDateFormat getSimpleDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten);
        return simpleDateFormat;
    }

    @ApiOperation(value = "在线用户报告")
    @GetMapping("/onlineUserReport")
    public Result<String> onlineUserReport() {
        String redisKey = SecurityConstants.REDIS_UNAME_TO_ACCESS+"online";
        Set<String> keySet = redisRepository.keys(redisKey+"*");
        Calendar nowTime = Calendar.getInstance();
        String format = getSimpleDateFormat().format(nowTime.getTime());
        OnlineUser onlineUser = new OnlineUser();
        onlineUser.setOnlineNum(keySet.size());
        onlineUser.setStaticsTimes(format);
        onlineUser.setStaticsDay(format.substring(0,10));
        onlineUserService.saveOnlineUser(onlineUser);
        return Result.succeed();
    }

}
