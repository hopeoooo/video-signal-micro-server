package com.central.common.constant;

/**
 * 国际化Keys
 *
 * @author lance
 * @since 2022 -01-25 15:42:26
 */
public interface I18nKeys {

    /**
     * 语种
     */
    interface Locale {
        /**
         * 简体中文
         */
        String ZH_CN = "zh_cn";

        /**
         * 英文
         */
        String EN_US = "en_us";

        /**
         * 高棉语
         */
        String KHM = "khm";

        /**
         * 泰语
         */
        String TH = "th";
    }

    // 语种代码
    interface LocaleCode {
        int ZH_CN = 0;
        int EN_US = 1;
        int KHM = 2;
        int TH = 3;
    }

    interface Redis {
        /**
         * 英文国际化 key
         */
        String EN_US_KEY = "i18n:source:en_us:hash";

        /**
         * 高棉语国际化 key
         */
        String KHM_KEY = "i18n:source:khm:hash";

        /**
         * 泰语国际化 key
         */
        String TH_KEY = "i18n:source:th:hash";
    }

}