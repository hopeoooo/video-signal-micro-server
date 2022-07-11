package com.central.common.redis.constant;

public interface RedisKeyConstant {

    /**
     * 锁等待时间
     */
    Integer WAIT_TIME = 120;
    Integer LEASE_TIME = 60;
    /**
     * 上下分
     */
    String SYS_USER_MONEY_MONEY_LOCK = "lock::sys_user_money::money::";
    /**
     * 洗码
     */
    String SYS_USER_MONEY_WASH_CODE_LOCK = "lock::sys_user_money::wash_code::";
    /**
     * 打码
     */
    String SYS_USER_MONEY_FLOW_CODE_LOCK = "lock::sys_user_money::flow_code::";
    /**
     * 即时彩池
     */
    String GAME_RECORD_LIVE_POT_LOCK = "lock::game_record::livePot::";
    /**
     * 即时彩池下注数据汇总
     */
    String GAME_RECORD_LIVE_POT_DATA = "cache::livePot::";
    /**
     * 每靴开奖结果汇总
     */
    String GAME_RECORD_LOTTERY_RESULT_DATA = "cache::lotteryResult::";
    /**
     * 每靴开奖结果汇总锁
     */
    String GAME_RECORD_LOTTERY_RESULT_LOCK = "lock::lotteryResult::";
    /**
     * 即时彩池下注总人数
     */
    String GAME_RECORD_LIVE_POT_BET_NUM_DATA = "cache::livePotBetNum::";
    /**
     * 现场版房间信息
     */
    String GAME_ROOM_INFO_OFFLINE_DATA = "cache::roomInfo::";
}
