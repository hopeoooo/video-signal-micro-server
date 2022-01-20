package com.central.platform.backend.controller;

import com.central.common.constant.SecurityConstants;
import com.central.common.model.PageResult2;
import com.central.common.model.Result;
import com.central.common.redis.template.RedisRepository;
import com.central.platform.backend.model.OnlineUser;
import com.central.platform.backend.service.IOnlineUserService;
import com.central.platform.backend.service.impl.OnlineUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线会员报表
 */
@Slf4j
@RestController
@Api(tags = "在线会员报表api")
@RequestMapping("/platform/user")
public class OnlineUserController {
    @Autowired
    private IOnlineUserService iOnlineUserService;

    @Autowired
    private RedisRepository redisRepository;

    private static String patten1 = "yyyy-MM-dd";

    private static String patten = "yyyy-MM-dd HH:mm:ss";

    public final static String start = " 00:00:00";

    public final static String end = " 23:59:59";

    public static SimpleDateFormat getSimpleDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten);
        return simpleDateFormat;
    }

    public static SimpleDateFormat getSimpleDateFormat1() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten1);
        return simpleDateFormat;
    }

    /**
     * 在线会员报表走势图查询
     */
    @ApiOperation(value = "在线会员报表走势图查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag", value = "tag:0 当日 tag:1 当月", required = true, dataType = "Integer"),
    })
    @GetMapping("/online/list")
    public Result<List> list(Integer tag) {
        Map params = new HashMap();
        Calendar nowTime = Calendar.getInstance();
        List<OnlineUser> onlineUserList = null;
        try {
            if (tag == null || tag == 0){
            String format = getSimpleDateFormat1().format(nowTime.getTime());
            String startTime = format + start;
            String endTime = format + end;
            Date start =getSimpleDateFormat().parse(startTime);
            Date end = getSimpleDateFormat().parse(endTime);
            params.put("startTime",start);
            params.put("endTime", end);
            onlineUserList = iOnlineUserService.findOnlineUserList(params);
            return Result.succeed(onlineUserList);
        }else {
             Calendar c = Calendar.getInstance();
             c.add(Calendar.MONTH, 0);
             c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
             String monthfirst = getSimpleDateFormat1().format(c.getTime());
             String startTime = monthfirst + start;
             String format = getSimpleDateFormat1().format(nowTime.getTime());
             String endTime = format + end;
             Date start =getSimpleDateFormat().parse(startTime);
             Date end = getSimpleDateFormat().parse(endTime);
             params.put("startTime",start);
             params.put("endTime", end);
             onlineUserList = iOnlineUserService.findOnlineUserList(params);
             if (onlineUserList == null || onlineUserList.size() == 0)
                 return Result.succeed(onlineUserList);
             Map<String, List<OnlineUser>> map = onlineUserList.stream().collect(Collectors.groupingBy(OnlineUser::getStaticsDay));
             Map<String,List<OnlineUser>> result = new TreeMap<>();
             map.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEachOrdered(x->result.put(x.getKey(),x.getValue()));
             map.clear();
             List<OnlineUser> list = new LinkedList<>();
             result.forEach((k,v)->{
                 Integer onlineNum = v.stream().mapToInt(OnlineUser::getOnlineNum).sum();
                 OnlineUser onlineUser = new OnlineUser();
                 onlineUser.setStaticsDay(k);
                 onlineUser.setOnlineNum(onlineNum/v.size());
                list.add(onlineUser);
             });
             onlineUserList.clear();
             result.clear();
             return Result.succeed(list);
        }
        }catch (Exception ex){
            log.error("查询在线人数失败");
        }
        return Result.failed("查询失败");
    }

    /**
     * 及时在线会员
     */
    @ApiOperation(value = "及时在线会员")
    @GetMapping("/online/queryPlayerNums")
    public Result<Integer> queryPlayerNums() {
        String redisKey = SecurityConstants.REDIS_UNAME_TO_ACCESS+"online";
        Set<String> keySet = redisRepository.keys(redisKey+"*");
        return Result.succeed(keySet.size());
    }

    /**
     * 会员报表查询
     */
    @ApiOperation(value = "会员报表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "startDate", value = "注册起始时间查询", required = true),
            @ApiImplicitParam(name = "endDate", value = "注册结束时间查询", required = true),
    })
    @GetMapping("/online/findPageList")
    public Result<PageResult2<OnlineUser>> findPageList(@RequestParam Map<String, Object> params) {
        PageResult2<OnlineUser> pageList = iOnlineUserService.findPageList(params);
        return Result.succeed(pageList);
    }
}
