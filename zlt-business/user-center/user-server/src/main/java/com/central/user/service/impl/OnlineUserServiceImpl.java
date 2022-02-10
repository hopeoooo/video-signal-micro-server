package com.central.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.params.user.OnlineUserParams;
import com.central.user.mapper.OnlineUserMapper;
import com.central.common.model.OnlineUser;
import com.central.user.service.IOnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OnlineUserServiceImpl implements IOnlineUserService {
    @Autowired
    private OnlineUserMapper onlineUserMapper;
    @Override
    public List<OnlineUser> findOnlineUserList(OnlineUserParams params) {
        return onlineUserMapper.findOnlineUserList(params);
    }


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

    @Override
    public List<OnlineUser> findOnlineUserMaps(Integer tag) {
        OnlineUserParams params = new OnlineUserParams();
        params.setTag(tag);
        Calendar nowTime = Calendar.getInstance();
        List<OnlineUser> onlineUserList = null;
        try {
            if (tag == null || tag == 0){
                String format = getSimpleDateFormat1().format(nowTime.getTime());
                String startTime = format + start;
                String endTime = format + end;
                Date start =getSimpleDateFormat().parse(startTime);
                Date end = getSimpleDateFormat().parse(endTime);
                params.setStart(start);
                params.setEndTime(end);
                return findOnlineUserList(params);
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
                params.setStart(start);
                params.setEndTime(end);
                onlineUserList = findOnlineUserList(params);
                if (onlineUserList == null || onlineUserList.size() == 0)
                    return onlineUserList;
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
                return list;
            }
        }catch (Exception ex){
            log.error("查询在线人数失败");
        }
        return onlineUserList;
    }

    @Override
    public PageResult<OnlineUser> findPageList(OnlineUserParams params) {
        Page<OnlineUser> page = new Page<>(params.getPage(), params.getLimit());
        List<OnlineUser> list = onlineUserMapper.findPageList(page, params);
        long total = page.getTotal();
        return PageResult.<OnlineUser>builder().data(list).count(total).build();
    }

    @Override
    public int saveOnlineUser(OnlineUser onlineUser) {
        return onlineUserMapper.saveOnlineUser(onlineUser);
    }
}
