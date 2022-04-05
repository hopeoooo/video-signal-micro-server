package com.central.push.constant;

/**
 * webSocket 消息类型
 *
 * @author zlt
 * @date 2018/10/29
 */
public interface SocketTypeConstant {

    /**
     * 客户端发送消息心跳
     */
    String HEARTBEAT = "heartbeat";

    /**
     * 用户金额
     */
    String MONEY = "money";

    /**
     * 轮播图
     */
    String BANNER = "banner";

    /**
     * 公告
     */
    String NOTICE = "notice";

    /**
     * 在线人数
     */
    String ONLINE_NUMS = "online_nums";

    /**
     * 即时彩池
     */
    String LIVE_POT = "livePot";

    /**
     * 桌台配置信息
     */
    String TABLE_INFO = "tableInfo";

    /**
     * 下注开奖结果
     */
    String LOTTER_RESULT = "lotterResult";

    /**
     * 派彩结果
     */
    String PAYOUT_RESULT = "payoutResult";


}
