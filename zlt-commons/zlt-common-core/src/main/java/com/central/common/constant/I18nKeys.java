package com.central.common.constant;

/**
 * 国际化Keys
 *
 * @author lance
 * @since 2022 -01-25 15:42:26
 */
public interface I18nKeys {

    String ZH_CN = "zh_cn";

    String EN_US = "en_us";

    String KHM = "khm";

    String TH = "th";

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
