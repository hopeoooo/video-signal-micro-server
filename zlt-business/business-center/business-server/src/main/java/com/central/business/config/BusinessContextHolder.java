package com.central.business.config;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 商户holder
 *
 * @author zlt
 * @date 2019/8/5
 */
public class BusinessContextHolder {
    /**
     * 支持父子线程之间的数据传递
     */
    private static final ThreadLocal<String> CONTEXT = new TransmittableThreadLocal<>();

    public static void setBusiness(String tenant) {
        CONTEXT.set(tenant);
    }

    public static String getBusiness() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
