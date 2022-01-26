package com.central.common.utils;

import cn.hutool.core.util.StrUtil;
import com.central.common.constant.I18nKeys;
import com.central.common.dto.I18nSourceDTO;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 国际化工具
 *
 * @author lance
 * @since 2022 -01-25 18:05:09
 */
@Component
public class I18nUtil implements ApplicationContextAware {

    private static RedisTemplate<String, String> redisTemplate;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        redisTemplate = applicationContext.getBean("stringRedisTemplate", RedisTemplate.class);
    }

    /**
     * 翻译
     *
     * @param language 语言
     * @param key      待翻译文本
     * @return {@link String} 出参释义
     * @author lance
     * @since 2022 -01-25 18:18:28
     */
    public static String translate(String language, String key) {
        if (I18nKeys.Locale.ZH_CN.equals(language)) {
            return key;
        }
        String value = redisTemplate.<String, String>opsForHash().get(keyOf(language), key);
        if (null == value) {
            return key;
        }
        return value;
    }

    /**
     * 翻译
     *
     * @param key      待翻译文本
     * @return {@link String} 出参释义
     * @author lance
     * @since 2022 -01-25 18:19:02
     */
    public static String t(String key) {
        HttpServletRequest request = ServletUtil.getHttpServletRequest();
        if (null == request) {
            return key;
        }
        String language = request.getHeader(I18nKeys.LANGUAGE);
        return translate(language, key);
    }

    /**
     * 重设国际化资源redis缓存
     *
     * @param language 入参释义
     * @param key      入参释义
     * @param value    入参释义
     * @author lance
     * @since 2022 -01-25 18:05:09
     */
    public static void resetSource(String language, String key, String value) {
        redisTemplate.opsForHash().put(
                keyOf(language),
                key,
                value
        );
    }

    /**
     * 获取对应语言的国际化资源
     *
     * @param language 入参释义
     * @return {@link Map} 出参释义
     * @author lance
     * @since 2022 -01-25 18:11:01
     */
    public static Map<String, String> getSource(String language) {
        HashOperations<String, String, String> ops = redisTemplate.<String, String>opsForHash();
        return ops.entries(keyOf(language));
    }

    // 找到对应语言的redis key
    private static String keyOf(String language) {
        if (StrUtil.isBlank(language)) {
            return I18nKeys.Redis.EN_US_KEY;
        }
        switch (language.toLowerCase()) {
            case I18nKeys.Locale.EN_US:
                return I18nKeys.Redis.EN_US_KEY;
            case I18nKeys.Locale.KHM:
                return I18nKeys.Redis.KHM_KEY;
            case I18nKeys.Locale.TH:
                return I18nKeys.Redis.TH_KEY;
        }
        return I18nKeys.Redis.EN_US_KEY;
    }

    /**
     * 获取所有语言的国际化资源
     *
     * @return {@link I18nSourceDTO} 出参释义
     * @author lance
     * @since 2022 -01-25 18:11:01
     */
    public static I18nSourceDTO getFullSource(){
        I18nSourceDTO dto = new I18nSourceDTO();
        HashOperations<String, String, String> ops = redisTemplate.<String, String>opsForHash();
        dto.setEnUs(ops.entries(I18nKeys.Redis.EN_US_KEY));
        dto.setKhm(ops.entries(I18nKeys.Redis.KHM_KEY));
        dto.setTh(ops.entries(I18nKeys.Redis.TH_KEY));
        return dto;
    }

}
