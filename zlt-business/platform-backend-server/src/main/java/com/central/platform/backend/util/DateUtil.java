package com.central.platform.backend.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {

    public final static String start = " 00:00:00";

    public final static String end = " 23:59:59";

    public final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public final static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getTimeString(Date date){
        String format = simpleDateFormat1.format(date);
        return format;
    }

    public static Map<String, Object> getToday(){
        Calendar nowTime = Calendar.getInstance();
        String today = simpleDateFormat.format(nowTime.getTime());
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",today+start);
        map.put("endTime",today+end);
        return map;
    }

    public static Map<String, Object> getYesterday(){
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.DATE, -1);
        String today = simpleDateFormat.format(nowTime.getTime());
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",today+start);
        map.put("endTime",today+end);
        return map;
    }

    public static Map<String, Object> getWeek(){
        Date monday = getWeekStartDate();
        String mondayStr = simpleDateFormat.format(monday);

        Date sunday = getWeekEndDate();
        String sundayStr = simpleDateFormat.format(sunday);
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",mondayStr+start);
        map.put("endTime",sundayStr+end);
        return map;
    }

    public static Map<String, Object> getLastWeek(){
        Date lastMonday = getLastWeekStartDate();
        String mondayStr = simpleDateFormat.format(lastMonday);

        Date lastSunday = getLastWeekEndDate();
        String sundayStr = simpleDateFormat.format(lastSunday);
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",mondayStr+start);
        map.put("endTime",sundayStr+end);
        return map;
    }

    public static Map<String, Object> getMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        String startMonth = simpleDateFormat.format(calendar.getTime());

        calendar.add(Calendar.MONTH,1);//?????????1???
        calendar.add(Calendar.DAY_OF_MONTH,-1);//??????????????????,???????????????????????????
        String endMonth = simpleDateFormat.format(calendar.getTime());
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",startMonth+start);
        map.put("endTime",endMonth+end);
        return map;
    }

    public static Map<String, Object> getLastMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,-1);
        String startMonth = simpleDateFormat.format(calendar.getTime());

        calendar.add(Calendar.MONTH,1);//?????????1???
        calendar.add(Calendar.DAY_OF_MONTH,-1);//??????????????????,???????????????????????????
        String endMonth = simpleDateFormat.format(calendar.getTime());
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",startMonth+start);
        map.put("endTime",endMonth+end);
        return map;
    }

    public static Map<String, Object> getNearlyTwoMonths(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,-1);
        String startMonth = simpleDateFormat.format(calendar.getTime());

        calendar.add(Calendar.MONTH,2);//?????????2???
        calendar.add(Calendar.DAY_OF_MONTH,-1);//??????????????????,???????????????????????????
        String endMonth = simpleDateFormat.format(calendar.getTime());
        Map<String, Object> map = new HashMap<>();
        map.put("startTime",startMonth+start);
        map.put("endTime",endMonth+end);
        return map;
    }
    // ??????????????????????????????
    public static Date getWeekStartDate() {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 1);
        return cal.getTime();
    }

    // ?????????????????????????????????
    public static Date getWeekEndDate() {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    // ?????????????????????
    public static Date getLastWeekStartDate() {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 1);
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }

    // ????????????????????????
    public static Date getLastWeekEndDate() {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 7);
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }
}
