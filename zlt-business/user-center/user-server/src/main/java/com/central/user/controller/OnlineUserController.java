package com.central.user.controller;

import com.central.common.constant.SecurityConstants;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.params.user.OnlineUserParams;
import com.central.common.redis.template.RedisRepository;
import com.central.common.model.OnlineUser;
import com.central.user.service.IOnlineUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

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
     * 查询在线人数
     *
     * @param params 入参释义
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -02-10 11:53:47
     */
    @ApiOperation(value = "查询在线人数")
    @GetMapping("/list")
    public Result<List<OnlineUser>> list(@ModelAttribute OnlineUserParams params){
        List<OnlineUser> onlineUserList = onlineUserService.findOnlineUserList(params);
        return Result.succeed(onlineUserList);
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
    public Result<PageResult<OnlineUser>> findPageList(@ModelAttribute OnlineUserParams params) {
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
